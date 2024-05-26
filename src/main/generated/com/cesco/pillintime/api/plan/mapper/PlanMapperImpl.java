package com.cesco.pillintime.api.plan.mapper;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.plan.dto.PlanDto;
import com.cesco.pillintime.api.plan.dto.RequestPlanDto;
import com.cesco.pillintime.api.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.api.plan.entity.Plan;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-25T19:13:56+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
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
        planDto.setCabinetIndex( plan.getCabinetIndex() );
        planDto.setWeekday( plan.getWeekday() );
        planDto.setTime( plan.getTime() );
        planDto.setStartAt( plan.getStartAt() );
        planDto.setEndAt( plan.getEndAt() );

        return planDto;
    }

    @Override
    public Plan toEntity(RequestPlanDto planDto) {
        if ( planDto == null ) {
            return null;
        }

        Plan plan = new Plan();

        plan.setMedicineId( planDto.getMedicineId() );
        plan.setMedicineName( planDto.getMedicineName() );
        plan.setCabinetIndex( planDto.getCabinetIndex() );
        plan.setStartAt( planDto.getStartAt() );
        plan.setEndAt( planDto.getEndAt() );

        return plan;
    }

    @Override
    public ResponsePlanDto toResponseDto(Plan plan) {
        if ( plan == null ) {
            return null;
        }

        ResponsePlanDto responsePlanDto = new ResponsePlanDto();

        responsePlanDto.setMedicineName( plan.getMedicineName() );
        responsePlanDto.setWeekday( plan.getWeekday() );
        responsePlanDto.setTime( plan.getTime() );
        responsePlanDto.setStartAt( plan.getStartAt() );
        responsePlanDto.setEndAt( plan.getEndAt() );

        return responsePlanDto;
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
