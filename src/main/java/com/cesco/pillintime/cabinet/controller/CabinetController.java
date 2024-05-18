package com.cesco.pillintime.cabinet.controller;

import com.cesco.pillintime.cabinet.dto.CabinetDto;
import com.cesco.pillintime.cabinet.dto.SensorDto;
import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.cabinet.service.CabinetService;
import com.cesco.pillintime.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cabinet")
@RequiredArgsConstructor
public class CabinetController {

    private final CabinetService cabinetService;

    @PostMapping
    public ResponseEntity<ResponseDto> createCabinet(@RequestBody CabinetDto cabinetDto) {
        cabinetService.createCabinet(cabinetDto);
        return ResponseUtil.makeResponse(200, "Success create case",  null);
    }

    @PostMapping("/sensor")
    public ResponseEntity<ResponseDto> getSensorData(@RequestBody SensorDto sensorDto) {
        cabinetService.getSensorData(sensorDto);
        return ResponseUtil.makeResponse(200, "Success post sensor", null);
    }

    @DeleteMapping("/{cabinetId}")
    public ResponseEntity<ResponseDto> deleteCabinet(@PathVariable Long cabinetId) {
        cabinetService.deleteCabinet(cabinetId);
        return ResponseUtil.makeResponse(200, "Success delete cabinet", null);
    }
}
