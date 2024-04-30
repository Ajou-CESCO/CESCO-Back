package com.cesco.pillintime.controller;

import com.cesco.pillintime.dto.LoginDto;
import com.cesco.pillintime.dto.MessageDto;
import com.cesco.pillintime.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<MessageDto> login(@RequestBody LoginDto loginDto) {
        MessageDto message = new MessageDto();
        HttpHeaders headers = new HttpHeaders();

        String token = authService.login(loginDto);

        message.setStatus(200);
        message.setMessage("Success login");
        message.setData(token);

        return new ResponseEntity<MessageDto> (message, headers, 200);
    }
}
