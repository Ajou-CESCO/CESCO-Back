package com.cesco.pillintime.relation.service;

import com.cesco.pillintime.api.relation.service.RelationService;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.member.service.MemberServiceTest;
import com.cesco.pillintime.api.relation.dto.RelationDto;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.api.relation.repository.RelationRepository;
import com.cesco.pillintime.api.request.entity.Request;
import com.cesco.pillintime.api.request.repository.RequestRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.security.SecurityUtil;
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
    private RelationService relationService;

    @BeforeEach
    void init() {
        relationRepository = mock(RelationRepository.class);
        requestRepository = mock(RequestRepository.class);
        relationService = new RelationService(requestRepository, relationRepository);
    }

    @Test
    void createRelation_Success() {
        System.out.println("createRelation_Success");
        // Given
        Member requestMember = MemberServiceTest.createMember();
        Member targetMember = MemberServiceTest.createMember();
        Request request = new Request(requestMember,targetMember.getPhone());

        when(requestRepository.findById(any())).thenReturn(Optional.of(request));

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
        Member requestMember = MemberServiceTest.createMember();
        Member targetMember = MemberServiceTest.createMember();
        Request request = new Request(requestMember,targetMember.getPhone());

        when(requestRepository.findById(any())).thenReturn(Optional.empty());

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
        verify(relationRepository, never()).save(any());
        verify(requestRepository, never()).delete(request);
    }

    @Test
    void getRelationList_Success_alone() {
        System.out.println("getRelationList_Success_alone");
        // Given
        Member tokenMember = MemberServiceTest.createMember();
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
        Assertions.assertNull(relationList1);
    }

    @Test
    void getRelationList_Success_together() {
        System.out.println("getRelationList_Success_together");
        // Given
        Member tokenMember = MemberServiceTest.createMember();
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