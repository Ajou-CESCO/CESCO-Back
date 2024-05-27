package com.cesco.pillintime.api.adverse.controller;

import com.cesco.pillintime.api.adverse.dto.AdverseDto;
import com.cesco.pillintime.api.adverse.service.AdverseService;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.response.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/adverse")
@RequiredArgsConstructor
public class AdverseController {

    private final AdverseService adverseService;

    @GetMapping
    public ResponseEntity<ResponseDto> search(@RequestParam(name = "drugName") String drugName) {
        List<AdverseDto> adverseDtoList = adverseService.search(drugName);
        return ResponseUtil.makeResponse(200, "Success login", adverseDtoList);
    }

}
