package com.cesco.pillintime.plan.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class PlanDto {

    private Long id;
    private Long memberId;
    private String medicineId;
    private String medicineName;
    private Integer cabinetIndex;
    private List<Integer> weekdayList;
    private Integer weekday;
    private List<LocalTime> timeList;
    private LocalTime time;
    private LocalDate startAt;
    private LocalDate endAt;

}
