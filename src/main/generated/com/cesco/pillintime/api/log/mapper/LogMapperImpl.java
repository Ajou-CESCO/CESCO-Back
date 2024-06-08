package com.cesco.pillintime.api.log.mapper;

import com.cesco.pillintime.api.log.dto.LogDto;
import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.plan.entity.Plan;
import java.time.LocalTime;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-08T13:10:03+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class LogMapperImpl implements LogMapper {

    @Override
    public LogDto toDto(Log log) {
        if ( log == null ) {
            return null;
        }

        LogDto logDto = new LogDto();

        logDto.setMemberId( logMemberId( log ) );
        logDto.setPlanId( logPlanId( log ) );
        logDto.setPlannedAt( logPlanTime( log ) );
        logDto.setMedicineName( logPlanMedicineName( log ) );
        logDto.setTakenStatus( map( log.getTakenStatus() ) );
        Integer cabinetIndex = logPlanCabinetIndex( log );
        if ( cabinetIndex != null ) {
            logDto.setCabinetIndex( cabinetIndex );
        }
        logDto.setId( log.getId() );

        return logDto;
    }

    private Long logMemberId(Log log) {
        if ( log == null ) {
            return null;
        }
        Member member = log.getMember();
        if ( member == null ) {
            return null;
        }
        Long id = member.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long logPlanId(Log log) {
        if ( log == null ) {
            return null;
        }
        Plan plan = log.getPlan();
        if ( plan == null ) {
            return null;
        }
        Long id = plan.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private LocalTime logPlanTime(Log log) {
        if ( log == null ) {
            return null;
        }
        Plan plan = log.getPlan();
        if ( plan == null ) {
            return null;
        }
        LocalTime time = plan.getTime();
        if ( time == null ) {
            return null;
        }
        return time;
    }

    private String logPlanMedicineName(Log log) {
        if ( log == null ) {
            return null;
        }
        Plan plan = log.getPlan();
        if ( plan == null ) {
            return null;
        }
        String medicineName = plan.getMedicineName();
        if ( medicineName == null ) {
            return null;
        }
        return medicineName;
    }

    private Integer logPlanCabinetIndex(Log log) {
        if ( log == null ) {
            return null;
        }
        Plan plan = log.getPlan();
        if ( plan == null ) {
            return null;
        }
        Integer cabinetIndex = plan.getCabinetIndex();
        if ( cabinetIndex == null ) {
            return null;
        }
        return cabinetIndex;
    }
}
