package com.cesco.pillintime.health.mapper;

import com.cesco.pillintime.health.dto.HealthDto;
import com.cesco.pillintime.health.entity.Health;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HealthMapper {

    HealthMapper INSTANCE = Mappers.getMapper(HealthMapper.class);

    Health toEntity(HealthDto memberDto);

    HealthDto toDto(Health member);
}
