package com.cesco.pillintime.controller;

import com.cesco.pillintime.dto.MessageDto;
import com.cesco.pillintime.dto.UserDto;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.entity.User;
import com.cesco.pillintime.service.UserService;
import org.aspectj.bridge.Message;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> joinUser(@RequestBody UserDto request) {
        return userService.joinUser(request);
    }

    @PostMapping("/sign") // 회원 가입
    public ResponseEntity<String> createUser(@RequestBody UserDto request) {
        return userService.createUser(request);
    }

    @GetMapping // 내 정보 조회
    public ResponseEntity<MessageDto> searchUserByUuid(){
        MessageDto message = new MessageDto();
        HttpHeaders headers = new HttpHeaders();

        User user = userService.searchUserByUuid();

        message.setStatus(200);
        message.setMessage("Success create request");
        message.setData(user);

        return new ResponseEntity<MessageDto> (message, headers, 200);
    }

    @GetMapping("/search") // 사용자 조회
    public ResponseEntity<?> searchUserByPhone(@RequestParam UserDto request){
        return userService.searchUserByPhone(request.getPhone());
    }

    @PutMapping // 내 정보 수정
    public ResponseEntity<String> updateUserById(@RequestBody UserDto request){
        return userService.updateUserById(request);
    }

    @DeleteMapping // 탈퇴
    public ResponseEntity<String> deleteUser(@RequestParam UserDto request){
        return userService.deleteUser(request);
    }
}
