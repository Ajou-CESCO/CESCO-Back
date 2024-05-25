package com.cesco.pillintime.member.service;

import com.cesco.pillintime.api.cabinet.entity.Cabinet;
import com.cesco.pillintime.api.member.service.MemberService;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.member.dto.MemberDto;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.mapper.MemberMapper;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.api.relation.repository.RelationRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.security.JwtUtil;
import com.cesco.pillintime.security.SecurityUtil;
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

    // 주입 받는 객체가 많아서 어노테이션 사용하지 않음
    @BeforeEach
    public void setUp() {
        memberRepository = mock(MemberRepository.class);
        jwtUtil = mock(JwtUtil.class);
        relationRepository = mock(RelationRepository.class);
        securityUtil = new SecurityUtil(relationRepository);
        memberService = new MemberService(memberRepository, jwtUtil, securityUtil);
    }

    // 사용 이유 : 데이터 처리 로직에서 오류 발생 (mock으로 생성하면 기본적으로 null로 생성됨.)
    public static Member createMember() {
        long longValue = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        Member member = new Member();
        member.setId(1L);
        member.setName(UUID.randomUUID().toString().replace("-", "").substring(0, 4));
        member.setSsn(String.format("%06d", longValue % 1000000) +"-"+ String.format("%07d", longValue % 10000000));
        member.setPhone("010"+"-"+String.format("%04d", longValue % 10000)+"-"+String.format("%04d", longValue % 1000));
        member.setManager(false);
        member.setSubscriber(true);
        member.setCabinet(mock(Cabinet.class));

        return member;
    }

    @Test
    void CreateUser_Success() {
        // Given
        MemberDto memberDto = MemberMapper.INSTANCE.toDto(createMember());
        Member member = new Member(memberDto.getName(), memberDto.getPhone(), memberDto.getSsn(), memberDto.isManager());

        String extoken = "memo";
            /*
            when(실행될 코드).thenReturn(반환 결과)
             */
        when(memberRepository.findByPhone(memberDto.getPhone())).thenReturn(Optional.empty());
        when(memberRepository.findBySsn(memberDto.getSsn())).thenReturn(Optional.empty());
        when(jwtUtil.createAccessToken(member)).thenReturn(extoken);

        // When
        String retoken = memberService.createUser(memberDto);

        // Then
        /*
        * assertEquals(예상 결과, 실제 결과)로 넣으면 된다.
        * verify(memberRepository,times(1)).findByPhone(memberDto.getPhone());
           = memberRepository.findByPhone(memberDto.getPhone())가 한 번 실행되었다.
        * */
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

        // 실행될 코드의 파라미터로 값을 지정하기 어렵다면 any()을 사용하자 <- 메타몽임
        // anyLong() 등 기본 타입에 대해 지원해줌. 하지만 실제 값은 null임
        when(memberRepository.findByPhone(anyString())).thenReturn(Optional.of(new Member()));

        // When
        CustomException exception = Assertions.assertThrows(CustomException.class,
                () -> memberService.createUser(memberDto));

        // Then
        /*
         * never() : 한 번도 실행되지 않는다. times(0)와 같은 의미
         * */
        Assertions.assertEquals(ErrorCode.ALREADY_EXISTS_PHONE, exception.getErrorCode());
        verify(memberRepository, times(1)).findByPhone(memberDto.getPhone());
        verify(memberRepository, times(0)).findBySsn(memberDto.getSsn());
        verify(memberRepository, never()).save(member);
    }
    @Test
    void getUserById_Success_null() {
        // Given
        Long targetId = null;
        Member member = createMember();

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        // 기본적으로 아래 코드를 수정하면 된다.
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
        verify(memberRepository, times(1)).findById(targetId);
        verify(relationRepository, times(1)).findByMember(requestMember);
    }
    @Test
    void getUserById_NotFoundMember_1L() {
        // Given
        Long targetId = 1L;
        Member requestMember = createMember();

        // 기본적으로 Repository에서 Optional.empty()을 반환하는 경우
        // 코드에 따라 다르지만 일반적으로 CustomException이 발생된다
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

    @Test
    void deleteUser_Success() {
        Member member = mock(Member.class);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        memberService.deleteUser();

        // memberRepository.delete() 1번만 호출되었는지 확인
        verify(memberRepository, times(1)).delete(member);
    }
    @Test
    void deleteUser_NotFoundMember() {

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.empty());

        CustomException customException = Assertions.assertThrows(CustomException.class,
                () -> memberService.deleteUser());

        // memberRepository.delete()가 호출되지 않았는지 확인
        Assertions.assertEquals(customException.getErrorCode(),ErrorCode.NOT_FOUND_USER);
        verify(memberRepository, never()).delete(any());
    }
}