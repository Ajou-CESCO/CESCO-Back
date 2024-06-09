package com.cesco.pillintime.api.health.mapper;

import com.cesco.pillintime.api.health.dto.HealthDto;
import com.cesco.pillintime.api.health.entity.Health;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-09T20:18:19+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
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
        healthDto.setCalorie( health.getCalorie() );
        healthDto.setHeartRate( health.getHeartRate() );
        healthDto.setSleepTime( health.getSleepTime() );

        return healthDto;
    }

    @Override
    public Health toEntity(HealthDto healthDto) {
        if ( healthDto == null ) {
            return null;
        }

        Health health = new Health();

        health.setId( healthDto.getId() );
        health.setSteps( healthDto.getSteps() );
        health.setCalorie( healthDto.getCalorie() );
        health.setHeartRate( healthDto.getHeartRate() );
        health.setSleepTime( healthDto.getSleepTime() );

        return health;
    }
}
