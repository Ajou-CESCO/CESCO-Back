package com.cesco.pillintime.plan.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.medicine.dto.MedicineDto;
import com.cesco.pillintime.medicine.service.MedicineService;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.plan.dto.PlanDto;
import com.cesco.pillintime.plan.entity.Plan;
import com.cesco.pillintime.plan.repository.PlanRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final MedicineService medicineService;

    public void createPlan(PlanDto planDto) {
        Long memberId = planDto.getMemberId();
        Long medicineId = Long.parseLong(planDto.getMedicineId());
        List<Integer> weekdayList = planDto.getWeekdayList();
        List<String> timeList = planDto.getTimeList();

        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        MedicineDto medicineDto = medicineService.getMedicineByMedicineId(medicineId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MEDICINE))
                .get(0);

        if (!requestMember.equals(targetMember)) {
            SecurityUtil.checkPermission(requestMember, targetMember);
            targetMember = requestMember;
        }

        List<Plan> planList = new ArrayList<>();
        for (Integer weekday : weekdayList) {
            for (String time : timeList) {
                Plan plan = new Plan(targetMember, medicineId, weekday, time);
                planList.add(plan);
                System.out.println(plan);
            }
        }

        planRepository.saveAll(planList);
    }
}
