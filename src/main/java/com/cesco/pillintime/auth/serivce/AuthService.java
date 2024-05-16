package com.cesco.pillintime.auth.serivce;

import com.cesco.pillintime.auth.dto.LoginDto;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public String login(LoginDto loginDto) {
        String name = loginDto.getName();
        String phone = loginDto.getPhone();
        String ssn = loginDto.getSsn();

        Member member = memberRepository.findByNameAndPhoneAndSsn(name, phone, ssn)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return jwtUtil.createAccessToken(member);
    }

}
