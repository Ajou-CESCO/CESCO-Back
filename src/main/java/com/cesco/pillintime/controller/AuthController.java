package com.cesco.pillintime.controller;

import com.cesco.pillintime.dto.LoginDto;
import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.service.AuthService;
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
        Object tokenAndUserType = authService.login(loginDto);
        return ResponseUtil.makeResponse(200, "Success login", tokenAndUserType);
    }
}
