package com.cesco.pillintime.controller;

import com.cesco.pillintime.dto.MessageDto;
import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class MemberController {

    private final MemberService memberService;
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<String> joinUser(@RequestBody MemberDto memberDto) {
        return memberService.joinUser(memberDto);
    }

    @PostMapping("/signup") // 회원 가입
    public ResponseEntity<MessageDto> createUser(@RequestBody MemberDto memberDto) {
        MessageDto message = new MessageDto();
        HttpHeaders headers = new HttpHeaders();

        memberService.createUser(memberDto);

        message.setStatus(200);
        message.setMessage("Success create request");

        return new ResponseEntity<MessageDto> (message, headers, 200);
    }

    @GetMapping // 내 정보 조회
    public ResponseEntity<MessageDto> getUserByUuid(@RequestParam(defaultValue = "") String uuid){
        MessageDto message = new MessageDto();
        HttpHeaders headers = new HttpHeaders();

        Member member = memberService.getUserByUuid(uuid);

        message.setStatus(200);
        message.setMessage("Success create request");
        message.setData(member);

        return new ResponseEntity<MessageDto> (message, headers, 200);
    }

    @PutMapping // 내 정보 수정
    public ResponseEntity<MessageDto> updateUserById(@RequestParam(defaultValue = "") String uuid, @RequestBody MemberDto memberDto){
        MessageDto message = new MessageDto();
        HttpHeaders headers = new HttpHeaders();

        Member member = memberService.updateUserByUuid(uuid, memberDto);

        message.setStatus(200);
        message.setMessage("Success create request");
        message.setData(member);

        return new ResponseEntity<MessageDto> (message, headers, 200);
    }

    @DeleteMapping // 탈퇴
    public ResponseEntity<MessageDto> deleteUser(){
        MessageDto message = new MessageDto();
        HttpHeaders headers = new HttpHeaders();

        memberService.deleteUser();

        message.setStatus(200);
        message.setMessage("Success create request");

        return new ResponseEntity<MessageDto> (message, headers, 200);
    }
}
