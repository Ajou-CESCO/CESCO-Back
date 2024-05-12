package com.cesco.pillintime.plan.controller;

import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.plan.dto.PlanDto;
import com.cesco.pillintime.plan.service.PlanService;
import com.cesco.pillintime.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dose/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<ResponseDto> createPlan(@RequestBody PlanDto planDto) {
        planService.createPlan(planDto);
        return ResponseUtil.makeResponse(200, "Success create plan", null);
    }
}
