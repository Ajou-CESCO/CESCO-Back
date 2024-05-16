package com.cesco.pillintime.auth.serivce;

import com.cesco.pillintime.auth.dto.LoginDto;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.util.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private MemberRepository memberRepository;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(memberRepository, jwtUtil);

    }

    @Test
    void login_Success() {
        // Given
        Member member = mock(Member.class);
        LoginDto loginDto = new LoginDto();
        loginDto.setName("asdf");
        loginDto.setPhone("010-1234-5678");
        loginDto.setSsn("123456-7890123");

        String extoken = "memo";

        when(memberRepository.findByNameAndPhoneAndSsn(any(),any(),any())).thenReturn(Optional.of(member));
        when(jwtUtil.createAccessToken(any())).thenReturn(extoken);

        // When
        String retoken = authService.login(loginDto);

        // Then
        verify(memberRepository,times(1)).findByNameAndPhoneAndSsn(any(),any(),any());
        Assertions.assertEquals(extoken, retoken);
    }

    @Test
    void login_NotFoundMember() {
        // Given
        Member member = mock(Member.class);
        LoginDto loginDto = new LoginDto();
        loginDto.setName("asdf");
        loginDto.setPhone("010-1234-5678");
        loginDto.setSsn("123456-7890123");

        String extoken = "memo";

        when(memberRepository.findByNameAndPhoneAndSsn(any(),any(),any())).thenReturn(Optional.empty());
        when(jwtUtil.createAccessToken(any())).thenReturn(extoken);

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> authService.login(loginDto));

        // Then
        verify(memberRepository,times(1)).findByNameAndPhoneAndSsn(any(),any(),any());
        Assertions.assertEquals(ErrorCode.NOT_FOUND_USER, customException.getErrorCode());
    }
}