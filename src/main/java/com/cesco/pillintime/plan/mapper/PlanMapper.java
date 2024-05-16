package com.cesco.pillintime.plan.mapper;

import com.cesco.pillintime.plan.dto.PlanDto;
import com.cesco.pillintime.plan.entity.Plan;
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

}
