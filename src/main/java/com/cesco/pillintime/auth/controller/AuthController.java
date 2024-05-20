package com.cesco.pillintime.auth.controller;

import com.cesco.pillintime.auth.dto.LoginDto;
import com.cesco.pillintime.auth.dto.SmsDto;
import com.cesco.pillintime.auth.serivce.AuthService;
import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<ResponseDto> login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        return ResponseUtil.makeResponse(200, "Success login", Map.of("access_token", token));
    }

    @PostMapping("/sms")
    public ResponseEntity<ResponseDto> getSmsCode(@RequestBody SmsDto smsDto) {
        String code = authService.getSmsCode(smsDto);
        return ResponseUtil.makeResponse(200, "Success get code", Map.of("code", code));
    }
}
