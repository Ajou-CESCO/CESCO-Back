package com.cesco.pillintime.api.member.controller;

import com.cesco.pillintime.api.member.dto.MemberDto;
import com.cesco.pillintime.api.member.service.MemberService;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.response.util.ResponseUtil;
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
    public ResponseEntity<ResponseDto> getUserById(@RequestParam(required = false) Long targetId){
        MemberDto member = memberService.getUserById(targetId);
        return ResponseUtil.makeResponse(200, "Success get member", member);
    }

    @PatchMapping // 내 정보 수정
    public ResponseEntity<ResponseDto> updateUserById(@RequestParam(required = false) Long targetId, @RequestBody MemberDto memberDto){
        MemberDto member = memberService.updateUserById(targetId, memberDto);
        return ResponseUtil.makeResponse(200, "Success update member", member);
    }

    @DeleteMapping // 탈퇴
    public ResponseEntity<ResponseDto> deleteUser(){
        memberService.deleteUser();
        return ResponseUtil.makeResponse(200, "Success delete member", null);
    }
}
