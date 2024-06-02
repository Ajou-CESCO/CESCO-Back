package com.cesco.pillintime.api.plan.service;

import com.cesco.pillintime.api.plan.dto.RequestPlanDto;
import com.cesco.pillintime.api.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.api.plan.mapper.PlanMapper;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.log.service.LogService;
import com.cesco.pillintime.api.medicine.service.MedicineService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.entity.Plan;
import com.cesco.pillintime.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
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

        // Plan 엔티티 생성 및 설정
        List<Plan> planList = new ArrayList<>();
        for (Integer weekday : weekdayList) {
            for (LocalTime time : timeList) {
                Plan plan = PlanMapper.INSTANCE.toPlanEntity(requestPlanDto, targetMember, weekday, time);
                planList.add(plan);
            }
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

    @Transactional
    public void deletePlanById(Long memberId, Long medicineId, int cabinetIndex) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        planRepository.findTargetPlan(targetMember, medicineId, cabinetIndex)
                .ifPresent(planRepository::deleteAll);
    }
}
