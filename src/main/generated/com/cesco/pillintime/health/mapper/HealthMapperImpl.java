package com.cesco.pillintime.health.mapper;

import com.cesco.pillintime.health.dto.HealthDto;
import com.cesco.pillintime.health.entity.Health;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-17T13:14:57+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 18.0.2 (Azul Systems, Inc.)"
)
public class HealthMapperImpl implements HealthMapper {

    @Override
    public HealthDto toDto(Health health) {
        if ( health == null ) {
            return null;
        }

        HealthDto healthDto = new HealthDto();

        healthDto.setId( health.getId() );
        healthDto.setSteps( health.getSteps() );
        healthDto.setCal( health.getCal() );
        healthDto.setSleepTime( health.getSleepTime() );

        return healthDto;
    }
}
