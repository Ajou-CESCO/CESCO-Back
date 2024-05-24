package com.cesco.pillintime.plan.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ResponsePlanDto {

    private String medicineName;
    private Integer weekday;
    private LocalTime time;

}