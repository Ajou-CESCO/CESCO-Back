package com.cesco.pillintime.member.service;

import com.cesco.pillintime.cabinet.entity.Cabinet;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.dto.MemberDto;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.mapper.MemberMapper;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.relation.entity.Relation;
import com.cesco.pillintime.relation.repository.RelationRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.util.JwtUtil;
import com.cesco.pillintime.util.SecurityUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class MemberServiceTest {

    private MemberService memberService;
    private MemberRepository memberRepository;
    private RelationRepository relationRepository;
    private SecurityUtil securityUtil;
    private JwtUtil jwtUtil;


    public static Member createMember() {
        long longValue = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        Member member = new Member();
        member.setId(1L);
        member.setName(UUID.randomUUID().toString().replace("-", "").substring(0, 4));
        member.setSsn(String.format("%06d", longValue % 1000000) +"-"+ String.format("%07d", longValue % 10000000));
        member.setPhone("010"+"-"+String.format("%04d", longValue % 10000)+"-"+String.format("%04d", longValue % 1000));
        member.setManager(true);
        member.setSubscriber(true);
        member.setCabinet(mock(Cabinet.class));

        return member;
    }
    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        jwtUtil = mock(JwtUtil.class);
        relationRepository = mock(RelationRepository.class);
        securityUtil = new SecurityUtil(relationRepository);
        memberService = new MemberService(memberRepository, jwtUtil, securityUtil);
    }
    @Test
    void CreateUser_Success() {
        // Given
        MemberDto memberDto = MemberMapper.INSTANCE.toDto(createMember());
        Member member = new Member(memberDto.getName(), memberDto.getPhone(), memberDto.getSsn(), memberDto.isManager());

        String extoken = "memo";

        when(memberRepository.findByPhone(memberDto.getPhone())).thenReturn(Optional.empty());
        when(memberRepository.findBySsn(memberDto.getSsn())).thenReturn(Optional.empty());
        when(jwtUtil.createAccessToken(member)).thenReturn(extoken);

        // When
        String retoken = memberService.createUser(memberDto);

        // Then
        Assertions.assertEquals(extoken, retoken);
        verify(memberRepository,times(1)).findByPhone(memberDto.getPhone());
        verify(memberRepository,times(1)).findBySsn(memberDto.getSsn());
        verify(memberRepository, times(1)).save(member);

    }
    @Test
    void createUser_ExistPhone() {
        // Given
        MemberDto memberDto = MemberMapper.INSTANCE.toDto(createMember());
        Member member = new Member(memberDto.getName(), memberDto.getPhone(), memberDto.getSsn(), memberDto.isManager());

        when(memberRepository.findByPhone(anyString())).thenReturn(Optional.of(new Member()));

        // When
        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> memberService.createUser(memberDto));

        // Then
        Assertions.assertEquals(ErrorCode.ALREADY_EXISTS_PHONE, exception.getErrorCode());
        verify(memberRepository, times(1)).findByPhone(memberDto.getPhone());
        verify(memberRepository, times(0)).findBySsn(memberDto.getSsn());
        verify(memberRepository, times(0)).save(member);
    }
    @Test
    void getUserById_Success_null() {
        // Given
        Long targetId = null;
        Member member = createMember();

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        // When
        MemberDto m = memberService.getUserById(targetId);

        // Then
        Assertions.assertEquals(MemberMapper.INSTANCE.toDto(member), m);
        verify(memberRepository, never()).findById(any());
    }
    @Test
    void getUserById_Success_1L() {
        // Given
        Long targetId = 1L;
        Member requestMember = createMember();
        Member targetMember = createMember();

        List<Relation> relationList = new ArrayList<>();
        relationList.add(0, new Relation(targetMember,requestMember));

        when(memberRepository.findById(targetId)).thenReturn(Optional.of(targetMember));
        when(relationRepository.findByMember(requestMember)).thenReturn(Optional.of(relationList));

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        memberService.getUserById(targetId);

        // Then
        verify(memberRepository, times(1)).findById(1L);
        verify(relationRepository, times(1)).findByMember(requestMember);
    }
    @Test
    void getUserById_NotFoundMember_1L() {
        // Given
        Long targetId = 1L;
        Member requestMember = createMember();

        when(memberRepository.findById(targetId)).thenReturn(Optional.empty());

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class,
                () -> memberService.getUserById(targetId));

        // Then
        Assertions.assertEquals(customException.getErrorCode(),ErrorCode.NOT_FOUND_USER);
        verify(memberRepository, times(1)).findById(any());
    }
    @Test
    void getUserById_NotFoundRelation_1L() {
        // Given
        Long targetId = 1L;
        Member requestMember = createMember();
        Member targetMember = createMember();

        when(memberRepository.findById(targetId)).thenReturn(Optional.of(targetMember));

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class,
                () -> memberService.getUserById(targetId));

        // Then
        Assertions.assertEquals(ErrorCode.INVALID_USER_ACCESS,customException.getErrorCode());
        verify(memberRepository,times(1)).findById(targetId);
    }
//    @Test
//    void updateUserById_Success_Null() {
//        // Given
//        MemberDto memberDto = new MemberDto();
//        Member member = new Member("kim","010-9637-0802","990127-1234567",true);
//        memberDto.setName("kim");
//        memberDto.setPhone("010-9637-0802");
//        memberDto.setSsn("990127-1234567");
//        memberDto.setGender(1);
//        memberDto.setManager(true);
//
//
//        CustomUserDetails userDetails = mock(CustomUserDetails.class);
//        Authentication authentication = mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        when(authentication.getName()).thenReturn("username");
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));
//
//        // When
//        MemberDto m = memberService.updateUserById(null,memberDto);
//
//        // Then
//        Assertions.assertEquals(memberDto,m);
//    }
//
//    @Test
//    void updateUserById_Success_1L() {
//        // Given
//        MemberDto memberDto = new MemberDto();
//        Member member = new Member("kim","010-9637-0802","990127-1234567",true);
//        memberDto.setName("kim");
//        memberDto.setPhone("010-9637-0802");
//        memberDto.setSsn("990127-1234567");
//        memberDto.setGender(1);
//        memberDto.setManager(true);
//
//        CustomUserDetails userDetails = mock(CustomUserDetails.class);
//        Authentication authentication = mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        when(authentication.getName()).thenReturn("username");
//        when(authentication.getPrincipal()).thenReturn(userDetails);
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));
//        MemberDto m =  null;
//        // When
//        try {
//            m = memberService.updateUserById(1L, memberDto);
//        } catch (CustomException e) {
//            System.out.println("e = " + e + e.getMessage());
//            // e = com.cesco.pillintime.exception.CustomException null < 해결해야 됨
//        }
//        // Then
////        Assertions.assertEquals(memberDto,m);
//    }

    @Test
    void deleteUser_Success() {
        Member member = mock(Member.class);

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        memberService.deleteUser();

        // memberRepository.delete() 1번만 호출되었는지 확인
        verify(memberRepository, times(1)).delete(member);
    }
    @Test
    void deleteUser_NotFoundMember() {

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.empty());
        CustomException customException = Assertions.assertThrows(CustomException.class,
                () -> memberService.deleteUser());

        // memberRepository.delete()가 호출되지 않았는지 확인
        Assertions.assertEquals(customException.getErrorCode(),ErrorCode.NOT_FOUND_USER);
        verify(memberRepository, never()).delete(any());
    }
}