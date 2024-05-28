package com.cesco.pillintime.api.health.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class HealthDto {

    private Long id;

    private Integer steps;

    private double cal;

    private LocalTime sleepTime;

    private LocalDateTime lastUpLoadTime;
}
