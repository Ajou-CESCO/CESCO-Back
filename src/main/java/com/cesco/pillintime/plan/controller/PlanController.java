package com.cesco.pillintime.plan.controller;

import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.plan.dto.PlanDto;
import com.cesco.pillintime.plan.service.PlanService;
import com.cesco.pillintime.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<ResponseDto> getPlanByMemberId(@RequestBody PlanDto planDto) {
        List<PlanDto> planDtoList = planService.getPlanByMemberId(planDto);
        return ResponseUtil.makeResponse(200, "Success get dose plan", planDtoList);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<ResponseDto> deletePlanById(@PathVariable Long planId) {
        planService.deletePlanById(planId);
        return ResponseUtil.makeResponse(200, "Success delete dose plan", null);
    }
}
