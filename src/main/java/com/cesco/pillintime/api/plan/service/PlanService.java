package com.cesco.pillintime.api.plan.service;

import com.cesco.pillintime.api.log.repository.LogRepository;
import com.cesco.pillintime.api.plan.dto.RequestPlanDto;
import com.cesco.pillintime.api.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.api.plan.mapper.PlanMapper;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.log.service.LogService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.entity.Plan;
import com.cesco.pillintime.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final LogRepository logRepository;
    private final LogService logService;
    private final SecurityUtil securityUtil;

    @Transactional
    public void createPlan(RequestPlanDto requestPlanDto) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(requestPlanDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        // 보호자가 자기 자신에 대해 복용 계획을 생성할 때
        if (targetMember.isManager()) {
            throw new CustomException(ErrorCode.INVALID_USERTYPE);
        }

        List<Integer> weekdayList = requestPlanDto.getWeekdayList();
        List<LocalTime> timeList = requestPlanDto.getTimeList();

        Long maxGroupId = planRepository.findMaxGroupId();
        Long newGroupId = (maxGroupId == null) ? 1L : maxGroupId + 1;

        // Plan 엔티티 생성 및 설정
        List<Plan> planList = new ArrayList<>();
        for (Integer weekday : weekdayList) {
            for (LocalTime time : timeList) {
                Plan plan = PlanMapper.INSTANCE.toPlanEntity(requestPlanDto, targetMember, weekday, time, newGroupId);
                planList.add(plan);
            }
        }

        // medicineAdverse에 '효능군중복' 키가 있을 경우 medicineSeries가 같은 기존 계획들을 가져오기
        if (requestPlanDto.getMedicineAdverse() != null && requestPlanDto.getMedicineAdverse().containsKey("효능군중복")) {
            String medicineSeries = requestPlanDto.getMedicineSeries();
            List<Plan> existingPlans = planRepository.findByMemberAndMedicineSeries(targetMember, medicineSeries);

            // 기존 계획들의 medicineAdverse에 '효능군중복' 키와 값을 추가
            String efficacyOverlapValue = requestPlanDto.getMedicineAdverse().get("효능군중복");
            for (Plan existingPlan : existingPlans) {
                Map<String, String> existingAdverse = existingPlan.getMedicineAdverse();
                if (existingAdverse == null) {
                    existingAdverse = new HashMap<>();
                }
                existingAdverse.put("효능군중복", efficacyOverlapValue);
                existingPlan.setMedicineAdverse(existingAdverse);
            }

            // 업데이트된 기존 계획들을 저장
            planRepository.saveAll(existingPlans);
        }

        planRepository.saveAll(planList);
        logService.createDoseLog();
    }

    public List<ResponsePlanDto> getPlanByMemberId(Long memberId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        List<ResponsePlanDto> planDtoList = new ArrayList<>();
        planRepository.findByMember(targetMember)
                .ifPresent((planList) -> {
                    planDtoList.addAll(PlanMapper.INSTANCE.toResponseDto(planList));
                });

        return planDtoList;
    }

    public void updatePlanByGroupId(RequestPlanDto requestPlanDto) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(requestPlanDto.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        }

        Long groupId = requestPlanDto.getGroupId();

        planRepository.findPlanByMemberIdAndGroupId(targetMember.getId(), groupId)
                .ifPresent((planList) -> {
                    // 수정하기에 앞서 기존 계획에 해당하는 예정된 기록이 있을 경우 모두 삭제
                    logRepository.findPlannedLog(targetMember, groupId)
                            .ifPresent(logRepository::deleteAll);

                    List<Integer> weekdayList = requestPlanDto.getWeekdayList();
                    List<LocalTime> timeList = requestPlanDto.getTimeList();

                    List<Plan> plansToRemove = new ArrayList<>();
                    List<Plan> plansToAdd = new ArrayList<>();

                    // 기존 계획의 시각, 요일이 변경된 리스트에 없을 경우 삭제
                    for (Plan plan : planList) {
                        if (!weekdayList.contains(plan.getWeekday()) || !timeList.contains(plan.getTime())) {
                            plansToRemove.add(plan);
                        }
                    }

                    if (!plansToRemove.isEmpty()) {
                        planRepository.deleteAll(plansToRemove);
                    }

                    // 기존 시각, 날짜 외 새로운 값이 입력되었을 경우 새롭게 생성
                    for (Integer weekday : weekdayList) {
                        for (LocalTime time : timeList) {
                            // 날짜, 시각 중복여부 확인
                            boolean exists = planList.stream()
                                    .anyMatch(plan -> plan.getWeekday().equals(weekday) && plan.getTime().equals(time));

                            // 기존에 없던 시각, 날짜일 경우 계획 생성
                            if (!exists) {
                                Plan newPlan = PlanMapper.INSTANCE.toPlanEntity(requestPlanDto, targetMember, weekday, time, groupId);
                                plansToAdd.add(newPlan);
                            }
                        }
                    }

                    planRepository.saveAll(plansToAdd);
                    logService.createDoseLog();
                });
    }

    @Transactional
    public void deletePlanById(Long memberId, Long groupId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        // 삭제할 계획들의 medicineSeries를 가져오기
        List<Plan> plansToDelete = planRepository.findTargetPlan(targetMember, groupId).orElse(Collections.emptyList());
        String medicineSeries = plansToDelete.isEmpty() ? null : plansToDelete.get(0).getMedicineSeries();

        // 계획들을 삭제
        logRepository.findPlannedLog(targetMember, groupId).ifPresent(logRepository::deleteAll);
        if (!plansToDelete.isEmpty()) {
            planRepository.deleteAll(plansToDelete);
        }

        // 동일한 medicineSeries를 가진 다른 계획들의 medicineAdverse에서 '효능군중복' 키 제거
        if (medicineSeries != null) {
            List<Plan> existingPlans = planRepository.findByMemberAndMedicineSeries(targetMember, medicineSeries);
            for (Plan existingPlan : existingPlans) {
                Map<String, String> medicineAdverse = existingPlan.getMedicineAdverse();
                if (medicineAdverse != null && medicineAdverse.containsKey("효능군중복")) {
                    medicineAdverse.remove("효능군중복");
                    existingPlan.setMedicineAdverse(medicineAdverse);
                }
            }
            planRepository.saveAll(existingPlans);
        }
    }

    @Transactional
    @Scheduled(cron = "0 1 0 * * *")
    public void deletePlanByCurrentDate() {
        LocalDate today = LocalDate.now();

        // 삭제할 계획들의 medicineSeries를 가져오기
        List<Plan> plansToDelete = planRepository.findInactivePlan(today).orElse(Collections.emptyList());
        Map<Long, String> memberMedicineSeriesMap = new HashMap<>();
        for (Plan plan : plansToDelete) {
            memberMedicineSeriesMap.put(plan.getMember().getId(), plan.getMedicineSeries());
        }

        // 계획들을 삭제
        if (!plansToDelete.isEmpty()) {
            planRepository.deleteAll(plansToDelete);
        }

//        // 동일한 medicineSeries를 가진 다른 계획들의 medicineAdverse에서 '효능군중복' 키 제거
//        for (Map.Entry<Long, String> entry : memberMedicineSeriesMap.entrySet()) {
//            Long memberId = entry.getKey();
//            String medicineSeries = entry.getValue();
//            List<Plan> existingPlans = planRepository.findByMemberAndMedicineSeries(memberId, medicineSeries);
//            for (Plan existingPlan : existingPlans) {
//                Map<String, String> medicineAdverse = existingPlan.getMedicineAdverse();
//                if (medicineAdverse != null && medicineAdverse.containsKey("효능군중복")) {
//                    medicineAdverse.remove("효능군중복");
//                    existingPlan.setMedicineAdverse(medicineAdverse);
//                }
//            }
//            planRepository.saveAll(existingPlans);
//        }
    }
}
