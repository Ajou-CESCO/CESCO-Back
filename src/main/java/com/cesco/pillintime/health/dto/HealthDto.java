package com.cesco.pillintime.health.dto;

import lombok.Data;

import java.sql.Time;
import java.time.LocalTime;

@Data
public class HealthDto {

    private Long id;

    private Integer steps;

    private double cal;

    private LocalTime sleepTime;

}
