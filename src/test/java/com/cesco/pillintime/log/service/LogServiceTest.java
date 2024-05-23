package com.cesco.pillintime.log.service;

import com.cesco.pillintime.api.log.dto.LogDto;
import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.log.repository.LogRepository;
import com.cesco.pillintime.api.log.service.LogService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.api.relation.repository.RelationRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.security.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LogServiceTest {
    private final RelationRepository relationRepository = mock(RelationRepository.class);

    @Mock
    private LogRepository logRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private SecurityUtil securityUtil;
    @InjectMocks
    private LogService logService;

    @Test
    void createDoseLog() {
    }

    @Test
    void getDoseLogByMemberId() {
        // Given
        Long targetId = 1L;
        Member requestMember = mock(Member.class);
        Member targetMember = mock(Member.class);
        when(memberRepository.findById(targetId)).thenReturn(Optional.of(targetMember));

        List<Relation> relationList = new ArrayList<>();
        relationList.add(new Relation(requestMember,targetMember));
        when(relationRepository.findByMember(requestMember)).thenReturn(Optional.of(relationList));

        List<Log> logList = new ArrayList<>();
        logList.add(0,any());
        when(logRepository.findByMemberAndPlannedAt(targetMember,any())).thenReturn(Optional.of(logList));
        
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));
        
        // When
        List<LogDto> logDtoList = logService.getDoseLogByMemberId(targetId);

        // Then
        verify(memberRepository, times(1)).findById(targetId);
        verify(relationRepository, times(1)).findByMember(requestMember);
        verify(logRepository, times(1)).findByMemberAndPlannedAt(targetMember,any());
    }

    @Test
    void updateDoseLogByCurrentTime() {
    }
}