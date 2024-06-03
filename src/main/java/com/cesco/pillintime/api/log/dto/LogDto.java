package com.cesco.pillintime.api.log.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class LogDto {

    private Long id;
    private Long memberId;
    private Long planId;
    private LocalTime plannedAt;
    private int cabinetIndex;
    private String medicineName;
    private Integer takenStatus;

}
