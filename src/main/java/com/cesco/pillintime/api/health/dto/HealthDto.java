package com.cesco.pillintime.api.health.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class HealthDto {

    private Long id;

    private Long steps;

    private String stepsMessage;

    private Long calorie;

    private String calorieMessage;

    private Long heartRate;

    private String heartRateMessage;

    private Long sleepTime;

    private String sleepTimeMessage;

}
