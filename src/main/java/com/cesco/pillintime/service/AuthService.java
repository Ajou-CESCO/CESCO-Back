package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.LoginDto;
import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.mapper.MemberMapper;
import com.cesco.pillintime.repository.MemberRepository;
import com.cesco.pillintime.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;


    @Transactional
    public String login(LoginDto loginDto) {
        String name = loginDto.getName();
        String phone = loginDto.getPhone();

        System.out.println(name);

        Member member = memberRepository.findByNameAndPhone(name, phone);

        System.out.println(member);
        if (member == null) {
            System.out.println("일치하는 사용자가 존재하지 않습니다");
//            throw new UsernameNotFoundException("일치하는 사용자가 존재하지 않니다");
        }

        MemberDto memberDto = MemberMapper.INSTANCE.toDto(member);
        return jwtUtil.createAccessToken(memberDto);
    }

}
