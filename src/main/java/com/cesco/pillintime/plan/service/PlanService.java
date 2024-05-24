package com.cesco.pillintime.plan.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.log.service.LogService;
import com.cesco.pillintime.medicine.dto.MedicineDto;
import com.cesco.pillintime.medicine.service.MedicineService;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.plan.dto.PlanDto;
import com.cesco.pillintime.plan.dto.RequestPlanDto;
import com.cesco.pillintime.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.plan.entity.Plan;
import com.cesco.pillintime.plan.mapper.PlanMapper;
import com.cesco.pillintime.plan.repository.PlanRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final MedicineService medicineService;
    private final LogService logService;
    private final SecurityUtil securityUtil;

    public void createPlan(RequestPlanDto planDto) {
        Long memberId = planDto.getMemberId();
        Long medicineId = Long.parseLong(planDto.getMedicineId());
        Integer cabinetIndex = planDto.getCabinetIndex();
        List<Integer> weekdayList = planDto.getWeekdayList();
        List<LocalTime> timeList = planDto.getTimeList();
        LocalDate startAt = planDto.getStartAt();
        LocalDate endAt = planDto.getEndAt();

        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        if (targetMember.isManager()) {
            throw new CustomException(ErrorCode.INVALID_USERTYPE);
        }

        MedicineDto medicineDto = medicineService.getMedicineByMedicineId(medicineId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEDICINE))
                .get(0);

        List<Plan> planList = new ArrayList<>();
        for (Integer weekday : weekdayList) {
            for (LocalTime time : timeList) {
                Plan plan = new Plan(targetMember, medicineDto, cabinetIndex, weekday, time, startAt, endAt);
                planList.add(plan);
            }
        }

        planRepository.saveAll(planList);
        logService.createDoseLog();
    }

    public List<ResponsePlanDto> getPlanByMemberId(RequestPlanDto inputPlanDto) {
        Long targetId = inputPlanDto.getMemberId();

        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));


        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        Optional<List<Plan>> planListOptional = planRepository.findByMember(targetMember);
        List<ResponsePlanDto> PlanDtoList = new ArrayList<>();

        planListOptional.ifPresent(plans -> {
            for (Plan plan : plans) {
                ResponsePlanDto PlanDto = PlanMapper.INSTANCE.toResponseDto(plan);
//                PlanDto.setTimeList(new ArrayList<>());
//                PlanDto.setWeekdayList(new ArrayList<>());
                PlanDto.setTime(LocalTime.MIDNIGHT);  // 자정으로 초기화
                PlanDto.setWeekday(0);                // 일요일을 기본값으로 초기화
                PlanDtoList.add(PlanDto);
            }
        });

        return PlanDtoList;
    }

    public void deletePlanById(Long planId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));

        Member targetMember = plan.getMember();

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        }

        planRepository.delete(plan);
    }

}
