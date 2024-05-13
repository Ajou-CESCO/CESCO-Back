package com.cesco.pillintime.member.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.dto.MemberDto;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.relation.repository.RelationRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.util.JwtUtil;
import com.cesco.pillintime.util.SecurityUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class MemberServiceTest {

    private MemberService memberService;

    private MemberRepository memberRepository;

    private RelationRepository relationRepository;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        jwtUtil = mock(JwtUtil.class);
        relationRepository = mock(RelationRepository.class);
        memberService = new MemberService(memberRepository, relationRepository, jwtUtil);
    }

    @Test
    void CreateUser_Success() {
        System.out.println("CreateUser_Success");
        // Given
        MemberDto memberDto = new MemberDto();
        memberDto.setName("John");
        memberDto.setPhone("010-1234-5678");
        memberDto.setSsn("123456-7890123");

        String extoken = "memo";

        // When
        when(memberRepository.findByPhone(memberDto.getPhone())).thenReturn(Optional.empty());
        when(jwtUtil.createAccessToken(memberDto)).thenReturn(extoken);

        String retoken = memberService.createUser(memberDto);
        // Then
        verify(memberRepository).findByPhone(memberDto.getPhone());
        Assertions.assertEquals(extoken, retoken);
    }

    @Test
    void createUser_Phone_Exception() {
        System.out.println("createUser_Phone_Exception");
        // Given
        MemberDto memberDto = new MemberDto();
        memberDto.setName("kim");
        memberDto.setPhone("010-9637-0802");
        memberDto.setSsn("990127-1234567");
        when(memberRepository.findByPhone(anyString())).thenReturn(Optional.of(new Member()));

        // When
        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> memberService.createUser(memberDto));

        // Then
        Assertions.assertEquals(ErrorCode.ALREADY_EXISTS_PHONE, exception.getErrorCode());
        verify(memberRepository).findByPhone(anyString());
    }

    @Test
    void getUserById_Success() {

    }

    @Test
    void updateUserById_Success() {
        // Given
        System.out.println("updateUserById_Success");
        MemberDto memberDto = new MemberDto();
        Member member = new Member("kim","010-9637-0802","990127-1234567",true);
        memberDto.setName("kim");
        memberDto.setPhone("010-9637-0802");
        memberDto.setSsn("990127-1234567");
        memberDto.setGender(1);
        memberDto.setManager(true);

        // Mock a CustomUserDetails object to simulate authentication
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock SecurityUtil.getCurrentMember() to return a valid member
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        // When
        MemberDto m = memberService.updateUserById(null,memberDto);
        // Then
        Assertions.assertEquals(memberDto,m);
    }



    @Test
    void deleteUser() {
    }
}