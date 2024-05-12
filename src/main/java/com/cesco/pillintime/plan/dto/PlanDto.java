package com.cesco.pillintime.plan.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlanDto {

    private Long memberId;
    private String medicineId;
    private List<Integer> weekdayList;
    private List<String> timeList;

}
