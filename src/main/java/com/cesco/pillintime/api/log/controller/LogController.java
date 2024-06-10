package com.cesco.pillintime.api.log.controller;

import com.cesco.pillintime.api.log.dto.LogResponseDto;
import com.cesco.pillintime.api.log.dto.SensorDto;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.api.log.service.LogService;
import com.cesco.pillintime.response.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dose/log")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @GetMapping
    public ResponseEntity<ResponseDto> getDoseLogByMemberId(@RequestParam Long memberId, LocalDate date) {
        LogResponseDto logResponseDto = logService.getDoseLogByMemberId(memberId, date);
        return ResponseUtil.makeResponse(200, "Success get dose log", logResponseDto);
    }

    @PatchMapping
    public ResponseEntity<ResponseDto> updateDoseLogByCabinet(@RequestBody SensorDto sensorDto) {
        logService.updateDoseLogByCabinet(sensorDto);
        return ResponseUtil.makeResponse(200, "Success post sensor", null);
    }
}
