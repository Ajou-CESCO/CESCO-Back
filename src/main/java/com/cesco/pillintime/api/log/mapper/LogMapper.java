package com.cesco.pillintime.api.log.mapper;

import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.log.dto.LogDto;
import com.cesco.pillintime.api.log.entity.TakenStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Mapper
public interface LogMapper {

    LogMapper INSTANCE = Mappers.getMapper(LogMapper.class);

    @Mapping(source = "member.id", target = "memberId")
    @Mapping(source = "plan.medicineId", target = "medicineId")
    @Mapping(source = "plannedAt", target = "plannedAt", qualifiedByName = "extractLocalTime")
    @Mapping(source = "plan.medicineName", target = "medicineName")
    @Mapping(source = "takenStatus", target = "takenStatus")
    @Mapping(source = "plan.cabinetIndex", target = "cabinetIndex")
    LogDto toDto(Log log);

    default Integer map(TakenStatus takenStatus) {
        return takenStatus.ordinal();
    }

    @Named("extractLocalTime")
    default LocalTime extractLocalTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalTime() : null;
    }

}
