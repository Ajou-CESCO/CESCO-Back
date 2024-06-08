package com.cesco.pillintime.member.service;

import com.cesco.pillintime.api.member.dto.MemberDto;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.mapper.MemberMapperImpl;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.member.service.MemberService;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.security.JwtUtil;
import com.cesco.pillintime.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private MemberService memberService;

    private Member guardian;
    private Member patient;

    @BeforeEach
    void setUp() {
        guardian = new Member();
        guardian.setId(1L);
        guardian.setName("guardian");
        guardian.setSsn("123123-1");
        guardian.setPhone("010-1234-1234");
        guardian.setManager(false);

        patient = new Member();
        patient.setId(2L);
        patient.setName("patient");
        patient.setSsn("789789-2");
        patient.setPhone("010-5678-5678");
        patient.setManager(true);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(authentication.getName()).thenReturn("guardian");
        lenient().when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
    }

    @Nested
    class 회원가입 {

        @Test
        void 회원가입() {
            MemberDto guardianDto = MemberMapperImpl.INSTANCE.toDto(guardian);
            // given
            when(memberRepository.findByPhone(guardianDto.getPhone())).thenReturn(Optional.empty());
            when(jwtUtil.createAccessToken(any(Member.class))).thenReturn("mockJwtToken");

            // when
            String jwtToken = memberService.createUser(guardianDto);

            // then
            assertEquals("mockJwtToken", jwtToken);
            verify(memberRepository, times(1)).findByPhone(guardianDto.getPhone());
            verify(memberRepository, times(1)).save(any(Member.class));
            verify(jwtUtil, times(1)).createAccessToken(any(Member.class));
        }

        @Test
        void 예외_회원가입_전화번호_중복() {
            // given
            MemberDto guardianDto = MemberMapperImpl.INSTANCE.toDto(guardian);
            when(memberRepository.findByPhone(guardianDto.getPhone())).thenReturn(Optional.of(new Member()));
            // when, then
            CustomException exception = assertThrows(CustomException.class, () -> {
                memberService.createUser(guardianDto);
            });

            assertEquals(ErrorCode.ALREADY_EXISTS_PHONE, exception.getErrorCode());
            verify(memberRepository, times(1)).findByPhone(guardianDto.getPhone());
            verify(memberRepository, never()).save(any(Member.class));
        }
    }

    @Nested
    class 회원정보조회 {

        @Test
        void 본인정보조회() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));

            // when
            MemberDto result = memberService.getUserById(null);

            // then
            assertNotNull(result);
            assertEquals(guardian.getName(), result.getName());
            verify(memberRepository, never()).findById(anyLong());
        }

        @Test
        void 타인정보조회() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(securityUtil.checkPermission(guardian, patient)).thenReturn(true);

            // when
            MemberDto result = memberService.getUserById(patient.getId());

            // then
            assertNotNull(result);
            assertEquals(patient.getName(), result.getName());
            verify(memberRepository, times(1)).findById(patient.getId());
            verify(securityUtil, times(1)).checkPermission(guardian, patient);
        }

        @Test
        void 예외_토큰만료() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.empty());

            // when, then
            CustomException exception = assertThrows(CustomException.class, () -> {
                memberService.getUserById(patient.getId());
            });

            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
            verify(memberRepository, never()).findById(anyLong());
            verify(securityUtil, never()).checkPermission(any(Member.class), any(Member.class));
        }

        @Test
        void 예외_없는ID조회() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                memberService.getUserById(patient.getId());
            });

            // then
            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
            verify(memberRepository, times(1)).findById(patient.getId());
            verify(securityUtil, never()).checkPermission(any(Member.class), any(Member.class));
        }
    }

    @Nested
    class 회원정보수정 {
//    @Test
//    void getUserById_Success_null() {
//        // Given
//        when(SecurityUtil.getCurrentMember())
//
//
//        // When
//        MemberDto m = memberService.getUserById(targetId);
//
//        // Then
//        assertEquals(MemberMapper.INSTANCE.toDto(member), m);
//        verify(memberRepository, never()).findById(any());
//    }
//    @Test
//    void getUserById_Success_1L() {
//        // Given
//        Long targetId = 1L;
//        Member guardian;
//        Member patient;
//
//        List<Relation> relationList = new ArrayList<>();
//        relationList.add(0, new Relation(patient,guardian));
//
//        when(memberRepository.findById(targetId)).thenReturn(Optional.of(patient));
//
//        Authentication authentication = mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        when(authentication.getName()).thenReturn("username");
//        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
//        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
//
//        // When
//        memberService.getUserById(targetId);
//
//        // Then
//        verify(memberRepository, times(1)).findById(targetId);
//    }
//    @Test
//    void getUserById_NotFoundMember_1L() {
//        // Given
//        Long targetId = 1L;
//        Member guardian;
//
//        // 기본적으로 Repository에서 Optional.empty()을 반환하는 경우
//        // 코드에 따라 다르지만 일반적으로 CustomException이 발생된다
//        when(memberRepository.findById(targetId)).thenReturn(Optional.empty());
//
//        Authentication authentication = mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        when(authentication.getName()).thenReturn("username");
//        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
//        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
//
//        // When
//        CustomException customException = assertThrows(CustomException.class,
//                () -> memberService.getUserById(targetId));
//
//        // Then
//        assertEquals(customException.getErrorCode(),ErrorCode.NOT_FOUND_USER);
//        verify(memberRepository, times(1)).findById(any());
//    }
//    @Test
//    void getUserById_NotFoundRelation_1L() {
//        // Given
//        Long targetId = 1L;
//        Member guardian;
//        Member patient;
//
//        when(memberRepository.findById(targetId)).thenReturn(Optional.of(patient));
//
//        Authentication authentication = mock(Authentication.class);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        when(authentication.getName()).thenReturn("username");
//        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
//        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
//
//        // When
//        CustomException customException = assertThrows(CustomException.class,
//                () -> memberService.getUserById(targetId));
//
//        // Then
//        assertEquals(ErrorCode.INVALID_USER_ACCESS,customException.getErrorCode());
//        verify(memberRepository,times(1)).findById(targetId);
//    }
    }

    @Nested
    class 회원탈퇴 {
        @Test
        void 탈퇴() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            // when
            memberService.deleteUser();
            // then
            verify(memberRepository, times(1)).delete(guardian);
        }
        // 이미 탈퇴한 회원의 경우 다른 예외가 생기지 않으므로 다른 테스트는 생략한다.
    }
}