package com.cesco.pillintime.api.log.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class LogDto {

    private Long id;
    private Long memberId;
    private Long medicineId;
    private String medicineName;
    private LocalTime plannedAt;
    private int cabinetIndex;
    private Integer takenStatus;

}
