package com.cesco.pillintime.api.plan.controller;

import com.cesco.pillintime.api.plan.dto.RequestPlanDto;
import com.cesco.pillintime.api.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.api.plan.service.PlanService;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.response.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dose/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @PostMapping
    public ResponseEntity<ResponseDto> createPlan(@RequestBody RequestPlanDto planDto) {
        planService.createPlan(planDto);
        return ResponseUtil.makeResponse(200, "Success create plan", null);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getPlanByMemberId(@RequestParam Long memberId) {
        List<ResponsePlanDto> planDtoList = planService.getPlanByMemberId(memberId);
        return ResponseUtil.makeResponse(200, "Success get dose plan", planDtoList);
    }

    @PatchMapping
    public ResponseEntity<ResponseDto> updatePlanByGroupId(@RequestBody RequestPlanDto requestPlanDto) {
        planService.updatePlanByGroupId(requestPlanDto);
        return ResponseUtil.makeResponse(200, "Success update dose plan", null);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDto> deletePlan(@RequestParam Long memberId, Long groupId) {
        planService.deletePlanById(memberId, groupId);
        return ResponseUtil.makeResponse(200, "Success delete dose plan", null);
    }
}
