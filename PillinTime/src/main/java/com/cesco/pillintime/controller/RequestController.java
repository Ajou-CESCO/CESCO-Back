package com.cesco.pillintime.controller;

import com.cesco.pillintime.dto.MessageDto;
import com.cesco.pillintime.dto.RequestDto;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.repository.RequestRepository;
import com.cesco.pillintime.service.RequestService;

import org.apache.coyote.Response;
import org.aspectj.bridge.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/request")
    public ResponseEntity<MessageDto> createRequest(@RequestBody RequestDto requestDto) {
        MessageDto message = new MessageDto();
        HttpHeaders headers = new HttpHeaders();

        Request request = requestService.createRequest(requestDto);

        message.setStatus(200);
        message.setMessage("Success create request");
        message.setData(request);

        return new ResponseEntity<MessageDto> (message, headers, 200);
    }

    @GetMapping("/request")
    public ResponseEntity<MessageDto> getRequest() {
        MessageDto message = new MessageDto();
        HttpHeaders headers = new HttpHeaders();

        List<Request> requestList = requestService.getRelatedRequest();

        message.setStatus(200);
        message.setMessage("Success get all request");
        message.setData(requestList);

        return new ResponseEntity<MessageDto> (message, headers, 200);
    }

}
