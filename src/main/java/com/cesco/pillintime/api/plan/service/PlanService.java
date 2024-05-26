package com.cesco.pillintime.api.plan.service;

import com.cesco.pillintime.api.plan.dto.RequestPlanDto;
import com.cesco.pillintime.api.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.api.plan.mapper.PlanMapper;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.log.service.LogService;
import com.cesco.pillintime.api.medicine.dto.MedicineDto;
import com.cesco.pillintime.api.medicine.service.MedicineService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.entity.Plan;
import com.cesco.pillintime.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final MedicineService medicineService;
    private final LogService logService;
    private final SecurityUtil securityUtil;

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

        if (targetMember.isManager()) {
            throw new CustomException(ErrorCode.INVALID_USERTYPE);
        }

        Long medicineId = Long.valueOf(requestPlanDto.getMedicineId());
        MedicineDto medicineDto = medicineService.getMedicineByMedicineId(medicineId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEDICINE))
                .get(0);

        List<Plan> planList = PlanMapper.INSTANCE.toEntity(requestPlanDto, medicineDto, targetMember);

        planRepository.saveAll(planList);
        logService.createDoseLog();
    }

    public List<ResponsePlanDto> getPlanByMemberId(String memberId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(Long.parseLong(memberId))
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
