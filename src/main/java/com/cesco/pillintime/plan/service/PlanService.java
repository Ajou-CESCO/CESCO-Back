package com.cesco.pillintime.plan.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.log.service.LogService;
import com.cesco.pillintime.medicine.dto.MedicineDto;
import com.cesco.pillintime.medicine.service.MedicineService;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.plan.dto.PlanDto;
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

    public void createPlan(PlanDto planDto) {
        Long memberId = planDto.getMemberId();
        Long medicineId = Long.parseLong(planDto.getMedicineId());
        List<Integer> weekdayList = planDto.getWeekdayList();
        List<LocalTime> timeList = planDto.getTimeList();
        LocalDate startAt = planDto.getStartAt();
        LocalDate endAt = planDto.getEndAt();

        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            SecurityUtil.checkPermission(requestMember, targetMember);
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
                Plan plan = new Plan(targetMember, medicineDto, weekday, time, startAt, endAt);
                planList.add(plan);
            }
        }

        planRepository.saveAll(planList);
        logService.createDoseLog();
    }

    public List<PlanDto> getPlanByMemberId(PlanDto inputPlanDto) {
        Long targetId = inputPlanDto.getMemberId();

        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));


        if (!requestMember.equals(targetMember)) {
            SecurityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        Optional<List<Plan>> planListOptional = planRepository.findByMember(targetMember);
        List<PlanDto> planDtoList = new ArrayList<>();

        planListOptional.ifPresent(plans -> {
            for (Plan plan : plans) {
                PlanDto planDto = PlanMapper.INSTANCE.toDto(plan);
                planDto.setTimeList(new ArrayList<>());
                planDto.setWeekdayList(new ArrayList<>());
                planDtoList.add(planDto);
            }
        });

        return planDtoList;
    }

    public void deletePlanById(Long planId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PLAN));

        Member targetMember = plan.getMember();

        if (!requestMember.equals(targetMember)) {
            SecurityUtil.checkPermission(requestMember, targetMember);
        }

        planRepository.delete(plan);
    }

}
