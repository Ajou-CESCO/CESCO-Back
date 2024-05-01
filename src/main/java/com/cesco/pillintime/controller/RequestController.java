package com.cesco.pillintime.controller;

import com.cesco.pillintime.dto.RequestDto;
import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.service.RequestService;
import com.cesco.pillintime.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/request")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<ResponseDto> createRequest(@RequestBody RequestDto requestDto) {
        Request request = requestService.createRequest(requestDto);
        return ResponseUtil.makeResponse(200, "Success create request", request);
    }

    @GetMapping
    public ResponseEntity<ResponseDto> getRequest() {
        List<Request> requestList = requestService.getRelatedRequest();
        return ResponseUtil.makeResponse(200, "Success get request", requestList);
    }

}