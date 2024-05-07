package com.cesco.pillintime.controller;

import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.dto.ResponseDto;
import com.cesco.pillintime.service.MemberService;
import com.cesco.pillintime.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping // 회원 가입
    public ResponseEntity<ResponseDto> createUser(@RequestBody MemberDto memberDto) {
        String token = memberService.createUser(memberDto);
        return ResponseUtil.makeResponse(200, "Success create member", Map.of("access_token", token));
    }

    @GetMapping // 내 정보 조회
    public ResponseEntity<ResponseDto> getUserByUuid(@RequestParam(name = "uuid", defaultValue = "") String uuid){
        MemberDto member = memberService.getUserByUuid(uuid);
        return ResponseUtil.makeResponse(200, "Success get member", member);
    }

    @PatchMapping // 내 정보 수정
    public ResponseEntity<ResponseDto> updateUserById(@RequestParam(name = "uuid", defaultValue = "") String uuid, @RequestBody MemberDto memberDto){
        MemberDto member = memberService.updateUserByUuid(uuid, memberDto);
        return ResponseUtil.makeResponse(200, "Success update member", member);
    }

    @DeleteMapping // 탈퇴
    public ResponseEntity<ResponseDto> deleteUser(){
        memberService.deleteUser();
        return ResponseUtil.makeResponse(200, "Success delete member", null);
    }
}
