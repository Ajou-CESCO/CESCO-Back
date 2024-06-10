package com.cesco.pillintime.api.cabinet.controller;

import com.cesco.pillintime.api.cabinet.service.CabinetService;
import com.cesco.pillintime.api.cabinet.dto.CabinetDto;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.response.util.ResponseUtil;
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

    @DeleteMapping("/{cabinetId}")
    public ResponseEntity<ResponseDto> deleteCabinet(@PathVariable Long cabinetId) {
        cabinetService.deleteCabinet(cabinetId);
        return ResponseUtil.makeResponse(200, "Success delete cabinet", null);
    }
}
