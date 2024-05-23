package com.cesco.pillintime.api.plan.mapper;

import com.cesco.pillintime.api.plan.dto.PlanDto;
import com.cesco.pillintime.api.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.api.plan.entity.Plan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface PlanMapper {

    PlanMapper INSTANCE = Mappers.getMapper(PlanMapper.class);

    @Mapping(source = "member.id", target = "memberId")
    @Mapping(target = "weekdayList", ignore = true)
    @Mapping(target = "timeList", ignore = true)
    PlanDto toDto(Plan plan);

//    RequestPlanDto toRequestPlanDto(Plan plan);

    ResponsePlanDto toResponseDto(Plan plan);
}
