package com.cesco.pillintime.plan.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class RequestPlanDto {

    private Long memberId;
    private String medicineId;
    private String medicineName;
    private Integer cabinetIndex;
    private List<Integer> weekdayList;
    private List<LocalTime> timeList;
    private LocalDate startAt;
    private LocalDate endAt;

}