package com.cesco.pillintime.plan.mapper;

import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.plan.dto.PlanDto;
import com.cesco.pillintime.plan.entity.Plan;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-19T16:41:02+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 18.0.2 (Amazon.com Inc.)"
)
public class PlanMapperImpl implements PlanMapper {

    @Override
    public PlanDto toDto(Plan plan) {
        if ( plan == null ) {
            return null;
        }

        PlanDto planDto = new PlanDto();

        planDto.setMemberId( planMemberId( plan ) );
        planDto.setId( plan.getId() );
        planDto.setMedicineId( plan.getMedicineId() );
        planDto.setMedicineName( plan.getMedicineName() );
        planDto.setWeekday( plan.getWeekday() );
        planDto.setTime( plan.getTime() );
        planDto.setStartAt( plan.getStartAt() );
        planDto.setEndAt( plan.getEndAt() );

        return planDto;
    }

    private Long planMemberId(Plan plan) {
        if ( plan == null ) {
            return null;
        }
        Member member = plan.getMember();
        if ( member == null ) {
            return null;
        }
        Long id = member.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
