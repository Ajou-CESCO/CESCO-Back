package com.cesco.pillintime.relation.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.relation.dto.RelationDto;
import com.cesco.pillintime.relation.entity.Relation;
import com.cesco.pillintime.relation.repository.RelationRepository;
import com.cesco.pillintime.request.entity.Request;
import com.cesco.pillintime.request.repository.RequestRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.util.SecurityUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.mockito.Mockito.*;

class RelationServiceTest {

    private RequestRepository requestRepository;
    private RelationRepository relationRepository;
    private MemberRepository memberRepository;

    private RelationService relationService;

    public static Member createMember() {
        long longValue = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        Member member = new Member();
        member.setId(1L);
        member.setName(UUID.randomUUID().toString().replace("-", "").substring(0, 4));
        member.setSsn(String.format("%06d", longValue % 1000000) +"-"+ String.format("%06d", longValue % 10000000));
        member.setPhone(String.format("%03d", longValue % 1000));
        member.setManager(true);
        member.setSubscriber(true);
        // 캐비넷

        return member;
    }

    @BeforeEach
    void init() {
        memberRepository = mock(MemberRepository.class);
        relationRepository = mock(RelationRepository.class);
        requestRepository = mock(RequestRepository.class);
        relationService = new RelationService(requestRepository, relationRepository, memberRepository);
    }

    @Test
    void createRelation_Success() {
        System.out.println("createRelation_Success");
        // Given
        Member requestMember = createMember();
        Member targetMember = createMember();
        Request request = new Request(requestMember.getId(),targetMember.getPhone());

        when(requestRepository.findById(any())).thenReturn(Optional.of(request));
        when(memberRepository.findById(requestMember.getId())).thenReturn(Optional.of(requestMember));

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        relationService.createRelation(1L);

        // Then
        verify(relationRepository,times(1)).save(any());
        verify(requestRepository, times(1)).delete(request);
    }

    @Test
    void createRelation_NotFoundRequest() {
        System.out.println("createRelation_NotFoundRequest");
        // Given
        Member requestMember = createMember();
        Member targetMember = createMember();
        Request request = new Request(requestMember.getId(),targetMember.getPhone());

        when(requestRepository.findById(any())).thenReturn(Optional.empty());
        when(memberRepository.findById(requestMember.getId())).thenReturn(Optional.of(requestMember));

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> relationService.createRelation(1L));

        // Then
        Assertions.assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_REQUEST);
        verify(memberRepository, never()).findById(any());
        verify(relationRepository, never()).save(any());
        verify(requestRepository, never()).delete(request);
    }

    @Test
    void createRelation_NotFoundMember() {
        System.out.println("createRelation_NotFoundMember");
        // Given
        Member requestMember = createMember();
        Member targetMember = createMember();
        Request request = new Request(null,targetMember.getPhone());

        when(requestRepository.findById(any())).thenReturn(Optional.of(request));
        when(memberRepository.findById(requestMember.getId())).thenReturn(Optional.empty());

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> relationService.createRelation(1L));

        // Then
        Assertions.assertEquals(customException.getErrorCode(), ErrorCode.NOT_FOUND_USER);
        verify(relationRepository, never()).save(any());
        verify(requestRepository, never()).delete(request);
    }

    @Test
    void getRelationList_Success_alone() {
        System.out.println("getRelationList_Success_alone");
        // Given
        Member tokenMember = createMember();
        when(relationRepository.findByMember(any())).thenReturn(Optional.empty());

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(tokenMember));

        // When
        List<RelationDto> relationList1 = relationService.getRelationList();

        // Then
        Assertions.assertEquals(relationList1, null);
    }

    @Test
    void getRelationList_Success_together() {
        System.out.println("getRelationList_Success_together");
        // Given
        Member tokenMember = createMember();
        List<Relation> relationList = new ArrayList<>();
        when(relationRepository.findByMember(any())).thenReturn(Optional.of(relationList));

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(tokenMember));

        // When
        List<RelationDto> relationList1 = relationService.getRelationList();

        // Then
        Assertions.assertEquals(relationList1, Collections.emptyList());
    }
}