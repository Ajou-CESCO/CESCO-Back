package com.cesco.pillintime.api.health.mapper;

import com.cesco.pillintime.api.health.dto.HealthDto;
import com.cesco.pillintime.api.health.entity.Health;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HealthMapper {

    HealthMapper INSTANCE = Mappers.getMapper(HealthMapper.class);

//    Health toEntity(HealthDto healthDto);

    HealthDto toDto(Health health);

    Health toEntity(HealthDto healthDto);
}
