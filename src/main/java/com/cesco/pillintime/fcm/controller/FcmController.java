package com.cesco.pillintime.fcm.controller;

import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.dto.FcmTokenDto;
import com.cesco.pillintime.fcm.service.FcmService;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.response.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/fcm")
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/push")
    public ResponseEntity<ResponseDto> sendPushAlarm(@RequestBody FcmRequestDto fcmRequestDto) throws IOException {
        fcmService.sendPushAlarm(fcmRequestDto, true);
        return ResponseUtil.makeResponse(200, "Success send push", null);
    }

    @PostMapping("/token")
    public ResponseEntity<ResponseDto> sendFcmToken(@RequestBody FcmTokenDto fcmTokenDto) {
        fcmService.sendFcmToken(fcmTokenDto);
        return ResponseUtil.makeResponse(200, "Success send token", null);
    }

}