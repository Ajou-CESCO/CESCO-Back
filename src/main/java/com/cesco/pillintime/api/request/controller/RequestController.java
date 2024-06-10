package com.cesco.pillintime.api.request.controller;

import com.cesco.pillintime.api.request.dto.RequestDto;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.api.request.entity.Request;
import com.cesco.pillintime.api.request.service.RequestService;
import com.cesco.pillintime.response.util.ResponseUtil;
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
        List<RequestDto> requestList = requestService.getRelatedRequest();
        return ResponseUtil.makeResponse(200, "Success get request", requestList);
    }

}