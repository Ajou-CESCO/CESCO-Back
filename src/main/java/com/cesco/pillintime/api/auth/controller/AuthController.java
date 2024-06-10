package com.cesco.pillintime.api.auth.controller;

import com.cesco.pillintime.api.auth.dto.LoginDto;
import com.cesco.pillintime.api.auth.dto.SmsDto;
import com.cesco.pillintime.api.auth.serivce.AuthService;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.response.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/logout")
    public ResponseEntity<ResponseDto> logout() {
        authService.logout();
        return ResponseUtil.makeResponse(200, "Success logout", null);
    }

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
