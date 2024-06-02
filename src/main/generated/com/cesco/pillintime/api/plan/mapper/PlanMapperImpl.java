package com.cesco.pillintime.api.plan.mapper;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.plan.dto.RequestPlanDto;
import com.cesco.pillintime.api.plan.entity.Plan;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-03T03:51:57+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class PlanMapperImpl implements PlanMapper {

    @Override
    public Plan toPlanEntity(RequestPlanDto requestPlanDto, Member member, Integer weekday, LocalTime time) {
        if ( requestPlanDto == null && member == null && weekday == null && time == null ) {
            return null;
        }

        Plan.PlanBuilder plan = Plan.builder();

        if ( requestPlanDto != null ) {
            plan.medicineId( requestPlanDto.getMedicineId() );
            plan.medicineName( requestPlanDto.getMedicineName() );
            plan.medicineSeries( requestPlanDto.getMedicineSeries() );
            Map<String, String> map = requestPlanDto.getMedicineAdverse();
            if ( map != null ) {
                plan.medicineAdverse( new LinkedHashMap<String, String>( map ) );
            }
            plan.cabinetIndex( requestPlanDto.getCabinetIndex() );
            plan.startAt( requestPlanDto.getStartAt() );
            plan.endAt( requestPlanDto.getEndAt() );
        }
        plan.member( member );
        plan.weekday( weekday );
        plan.time( time );

        return plan.build();
    }
}
