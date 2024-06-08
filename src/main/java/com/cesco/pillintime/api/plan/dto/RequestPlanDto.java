package com.cesco.pillintime.api.plan.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Data
public class RequestPlanDto {

    private Long memberId;
    private Long groupId;
    private String medicineId;
    private String medicineName;
    private String medicineSeries;
    private Map<String, String> medicineAdverse;
    private Integer cabinetIndex;
    private List<Integer> weekdayList;
    private List<LocalTime> timeList;
    private LocalDate startAt;
    private LocalDate endAt;

}