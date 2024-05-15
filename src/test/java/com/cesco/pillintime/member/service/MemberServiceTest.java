package com.cesco.pillintime.member.service;

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

    private JwtUtil jwtUtil;

    public static MemberDto createMemberDto() {
        long longValue = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        MemberDto memberDto = new MemberDto();
        memberDto.setId(1L);
        memberDto.setName(UUID.randomUUID().toString().replace("-", "").substring(0, 4));
        memberDto.setSsn(String.format("%06d", longValue % 1000000) +"-"+ String.format("%06d", longValue % 10000000));
        memberDto.setPhone(String.format("%03d", longValue % 1000));
        memberDto.setManager(true);
        memberDto.setHasCase(true);
        memberDto.setSubscriber(true);

        return memberDto;
    }

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
    void createUser_ExistPhone() {
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
    void getUserById_Success_null() {
        // Given
        System.out.println("getUserById_Success_null");
        MemberDto memberDto = createMemberDto();

        Member member = MemberMapper.INSTANCE.toEntity(memberDto);

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(SecurityUtil.getCurrentMemberId()).thenReturn(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.ofNullable(member));

        // When
        MemberDto m = memberService.getUserById(null);

        // Then
        Assertions.assertEquals(memberDto,m);
        verify(memberRepository, times(1)).findById(any());
    }
    @Test
    void getUserById_Success_1L() {
        // Given
        System.out.println("getUserById_Success_1L");
        Member member1 = MemberMapper.INSTANCE.toEntity(createMemberDto());
        Member member2 = MemberMapper.INSTANCE.toEntity(createMemberDto());

        List<Relation> relationList = new ArrayList<>();
        relationList.add(0,new Relation(member1,member2));

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMemberId()).thenReturn(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member1));
        when(relationRepository.findByMember(any())).thenReturn(Optional.of(relationList));

        // When
        memberService.getUserById(1L);

        // Then
        verify(memberRepository, times(2)).findById(any());
    }
    @Test
    void getUserById_NotFoundMember_null() {
        // Given
        System.out.println("getUserById_NotFoundMember_null");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(SecurityUtil.getCurrentMemberId()).thenReturn(1L);
        when(memberRepository.findById(any())).thenReturn(Optional.empty());

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> memberService.getUserById(null));
        // Then
        Assertions.assertEquals(customException.getErrorCode(),ErrorCode.NOT_FOUND_USER);
        verify(memberRepository, times(1)).findById(any());
    }
    @Test
    void getUserById_NotFoundMember_1L() {
        // Given
        System.out.println("getUserById_NotFoundMember_1L");
        Member member1 = MemberMapper.INSTANCE.toEntity(createMemberDto());
        Member member2 = MemberMapper.INSTANCE.toEntity(createMemberDto());
        List<Relation> relationList = new ArrayList<>();
        relationList.add(0,new Relation(member1,member2));

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMemberId()).thenReturn(member1.getId());

        when(memberRepository.findById(member1.getId())).thenReturn(Optional.of(member1));
        when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(new Member()));
        when(relationRepository.findByMember(any())).thenReturn(Optional.of(relationList));

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> memberService.getUserById(2L));

        // Then
        Assertions.assertEquals(customException.getErrorCode(),ErrorCode.NOT_FOUND_USER);
        verify(memberRepository, times(2)).findById(any());
    }
    @Test
    void getUserById_NotFoundRelation_1L() {
        // Given
        System.out.println("getUserById_NotFoundUser_1L");
        Member member1 = MemberMapper.INSTANCE.toEntity(createMemberDto());
        Member member2 = MemberMapper.INSTANCE.toEntity(createMemberDto());

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMemberId()).thenReturn(member1.getId());

        when(memberRepository.findById(member1.getId())).thenReturn(Optional.of(member1));
        when(memberRepository.findById(member2.getId())).thenReturn(Optional.of(new Member()));
        when(relationRepository.findByMember(any())).thenReturn(Optional.empty());

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> memberService.getUserById(1L));

        // Then
        Assertions.assertEquals(customException.getErrorCode(),ErrorCode.INVALID_USER_ACCESS);
        verify(relationRepository, times(1)).findByMember(any());
    }
//    @Test
//    void updateUserById_Success_Null() {
//        // Given
//        System.out.println("updateUserById_Success");
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
//        System.out.println("updateUserById_Success");
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
        System.out.println("deleteUser_Success");
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
        System.out.println("deleteUser_NotFoundMember");

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.empty());
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> memberService.deleteUser());

        // memberRepository.delete()가 호출되지 않았는지 확인
        Assertions.assertEquals(customException.getErrorCode(),ErrorCode.NOT_FOUND_USER);
        verify(memberRepository, never()).delete(any());
    }
}