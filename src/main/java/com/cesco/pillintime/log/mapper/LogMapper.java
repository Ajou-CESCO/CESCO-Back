package com.cesco.pillintime.log.mapper;

import com.cesco.pillintime.log.dto.LogDto;
import com.cesco.pillintime.log.entity.Log;
import com.cesco.pillintime.log.entity.TakenStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LogMapper {

    LogMapper INSTANCE = Mappers.getMapper(LogMapper.class);

    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "plan.time", target = "plannedAt")
    @Mapping(source = "plan.medicineName", target = "medicineName")
    @Mapping(source = "takenStatus", target = "takenStatus")
    LogDto toDto(Log log);

    default Integer map(TakenStatus takenStatus) {
        return takenStatus.ordinal();
    }

}
