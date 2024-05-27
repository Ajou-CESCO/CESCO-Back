package com.cesco.pillintime.api.plan.mapper;

import com.cesco.pillintime.api.medicine.dto.MedicineDto;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.plan.dto.RequestPlanDto;
import com.cesco.pillintime.api.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.api.plan.entity.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Mapper
public interface PlanMapper {

    PlanMapper INSTANCE = Mappers.getMapper(PlanMapper.class);

    default List<Plan> toEntity(RequestPlanDto requestPlanDto, MedicineDto medicineDto, Member member) {
        // 필요한 값 설정
        Integer cabinetIndex = requestPlanDto.getCabinetIndex();
        List<Integer> weekdayList = requestPlanDto.getWeekdayList();
        List<LocalTime> timeList = requestPlanDto.getTimeList();
        LocalDate startAt = requestPlanDto.getStartAt();
        LocalDate endAt = requestPlanDto.getEndAt();

        // Plan 엔티티 생성 및 설정
        List<Plan> planList = new ArrayList<>();
        for (Integer weekday : weekdayList) {
            for (LocalTime time : timeList) {
                Plan plan = new Plan(member, medicineDto, cabinetIndex, weekday, time, startAt, endAt);
                planList.add(plan);
            }
        }

        return planList;
    }

    default List<ResponsePlanDto> toResponseDto(List<Plan> planList) {
        List<ResponsePlanDto> planDtoList = new ArrayList<>();

        // Plan 리스트를 medicineId와 cabinetIndex 기준으로 그룹화
        Map<String, Map<Integer, List<Plan>>> groupedPlans = planList.stream()
                .collect(Collectors.groupingBy(
                        Plan::getMedicineId,
                        Collectors.groupingBy(Plan::getCabinetIndex)
                ));

        // 그룹화된 데이터를 ResponsePlanDto로 변환
        for (Map.Entry<String, Map<Integer, List<Plan>>> medicineEntry : groupedPlans.entrySet()) {
            String medicineId = medicineEntry.getKey();
            Map<Integer, List<Plan>> cabinetMap = medicineEntry.getValue();

            for (Map.Entry<Integer, List<Plan>> cabinetEntry : cabinetMap.entrySet()) {
                Integer cabinetIndex = cabinetEntry.getKey();
                List<Plan> plans = cabinetEntry.getValue();

                if (!plans.isEmpty()) {
                    Plan firstPlan = plans.get(0); // 같은 그룹의 대표 Plan 하나 선택
                    ResponsePlanDto responsePlanDto = new ResponsePlanDto();
                    responsePlanDto.setMedicineId(medicineId);
                    responsePlanDto.setMedicineName(firstPlan.getMedicineName());
                    responsePlanDto.setCabinetIndex(cabinetIndex);
                    responsePlanDto.setStartAt(firstPlan.getStartAt());
                    responsePlanDto.setEndAt(firstPlan.getEndAt());

                    // 중복값 제거를 위해 Set -> List 변환 수행
                    Set<LocalTime> timeSet = new HashSet<>();
                    Set<Integer> weekdaySet = new HashSet<>();

                    for (Plan plan : plans) {
                        timeSet.add(plan.getTime());
                        weekdaySet.add(plan.getWeekday());
                    }

                    responsePlanDto.setTimeList(new ArrayList<>(timeSet));
                    responsePlanDto.setWeekdayList(new ArrayList<>(weekdaySet));

                    planDtoList.add(responsePlanDto);
                }
            }
        }

        return planDtoList;
    }
}
