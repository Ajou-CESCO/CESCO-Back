package com.cesco.pillintime.mapper;

import com.cesco.pillintime.dto.HealthDto;
import com.cesco.pillintime.entity.Health;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HealthMapper {

    HealthMapper INSTANCE = Mappers.getMapper(HealthMapper.class);

    Health toEntity(HealthDto memberDto);

    HealthDto toDto(Health member);
}
