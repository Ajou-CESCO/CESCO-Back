package com.cesco.pillintime.controller;

import com.cesco.pillintime.dto.HealthDto;
import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.service.HealthService;
import com.cesco.pillintime.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @PostMapping
    public ResponseEntity<ResponseDto> createHealth(@RequestBody HealthDto healthDto) {
        healthService.createHealth(healthDto);
        return ResponseUtil.makeResponse(200, "Success upload health data", null);
    }

    @PostMapping
    public ResponseEntity<ResponseDto> getHealthById(@RequestBody String uuid) {
        HealthDto healthDto = healthService.getHealthById(uuid);
        return ResponseUtil.makeResponse(200, "Success inquiry data", healthDto);
    }

    @PostMapping
    public ResponseEntity<ResponseDto> getHealth(@RequestBody String uuid) {
        List<HealthDto> healthDto = healthService.getHealth(uuid);
        return ResponseUtil.makeResponse(200, "Success Weekly Health data", healthDto);
    }
}
