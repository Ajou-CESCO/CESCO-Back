package com.cesco.pillintime.plan.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PlanDto {

    private Long id;
    private Long memberId;
    private String medicineId;
    private String medicineName;
    private List<Integer> weekdayList;
    private Integer weekday;
    private List<String> timeList;
    private String time;
    private LocalDate startedAt;
    private LocalDate endedAt;

}
