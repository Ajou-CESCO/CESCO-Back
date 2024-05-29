package com.cesco.pillintime.api.health.dto;

import lombok.Data;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class HealthDto {

    private Long id;

    private Integer steps;

    private String stepsMessage;

    private double cal;

    private LocalTime sleepTime;

    private String sleepTimeMessage;

    private LocalDateTime lastUpLoadTime;

    private Integer averageSteps;
}
