package com.cesco.pillintime.api.health.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class HealthDto {

    private Long id;

    private Long ageGroup;

    private Long steps;

    private Long averStep;

    private String stepsMessage;

    private Long calorie;

    private String calorieMessage;

    private Long heartRate;

    private String heartRateMessage;

    private Long sleepTime;

    private Long recommendSleepTime;

    private String sleepTimeMessage;

}
