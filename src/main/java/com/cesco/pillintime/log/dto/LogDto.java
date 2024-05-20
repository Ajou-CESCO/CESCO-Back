package com.cesco.pillintime.log.dto;

import com.cesco.pillintime.log.entity.TakenStatus;
import com.cesco.pillintime.plan.entity.Plan;
import lombok.Data;

import java.time.LocalTime;

@Data
public class LogDto {

    private Long id;
    private Long memberId;
    private Long planId;
    private LocalTime plannedAt;
    private String medicineName;
    private Integer takenStatus;

}
