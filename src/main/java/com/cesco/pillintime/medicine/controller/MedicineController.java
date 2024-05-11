package com.cesco.pillintime.medicine.controller;

import com.cesco.pillintime.medicine.dto.MedicineDto;
import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.medicine.service.MedicineService;
import com.cesco.pillintime.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/medicine")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineService medicineService;

    @GetMapping
    public ResponseEntity<ResponseDto> getMedicineInfo(@RequestParam(name = "name") String name) {
        List<MedicineDto> medicineDtoList = medicineService.getMedicineInfoByName(name);
        return ResponseUtil.makeResponse(200, "Success get medicine", medicineDtoList);
    }
}
