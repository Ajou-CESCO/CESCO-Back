package com.cesco.pillintime.health.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.health.dto.HealthDto;
import com.cesco.pillintime.health.entity.Health;
import com.cesco.pillintime.health.mapper.HealthMapper;
import com.cesco.pillintime.health.repository.HealthRepository;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.security.CustomUserDetails;
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

import static org.aspectj.runtime.internal.Conversions.intValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HealthServiceTest {

    private HealthRepository healthRepository;
    private MemberRepository memberRepository;
    private HealthService healthService;

    public static Health createHealthObject() {
        long longValue = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        Health health = new Health();
        health.setId(intValue(longValue) % 10);
        health.setCal(15.7);

        return health;
    }

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        healthRepository = mock(HealthRepository.class);
        healthService = new HealthService(healthRepository, memberRepository);
    }

    @Test
    void createHealth_Success() {
        Health health = createHealthObject();
        Member member = mock(Member.class);

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        // When
        healthService.createHealth(HealthMapper.INSTANCE.toDto(health));

        // Then
        verify(healthRepository, times(1)).save(any());
    }

    @Test
    void getHealthByMemberId_Success_1L() {
        Health health = mock(Health.class);
        Member requestMember = mock(Member.class);
        Member targetMember = mock(Member.class);
        List<Health> healthList = new ArrayList<>();
        healthList.add(health);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(targetMember));
        when(healthRepository.findByMember(targetMember)).thenReturn(Optional.of(healthList));

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        List<HealthDto> healthDtoList = healthService.getHealthByMemberId(1L);

        // Then
        System.out.println("healthList = " + healthDtoList);
        verify(healthRepository, times(1)).findByMember(any());
        verify(memberRepository, times(1)).findById(any());
    }

    @Test
    void getHealthByMemberId_Success_Null() {
        Health health = mock(Health.class);
        Member requestMember = mock(Member.class);
        Member targetMember = mock(Member.class);
        List<Health> healthList = new ArrayList<>();
        healthList.add(health);

        when(healthRepository.findByMember(requestMember)).thenReturn(Optional.of(healthList));

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        healthService.getHealthByMemberId(null);

        // Then
        verify(healthRepository, times(1)).findByMember(any());
        verify(memberRepository, never()).findById(any());
    }

    @Test
    void getHealthByMemberId_UserNotFound() {
        Health health = mock(Health.class);
        Member requestMember = mock(Member.class);
        List<Health> healthList = new ArrayList<>();
        healthList.add(health);

        when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        when(healthRepository.findByMember(requestMember)).thenReturn(Optional.of(healthList));

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> healthService.getHealthByMemberId(1L));

        // Then
        Assertions.assertEquals(ErrorCode.NOT_FOUND_USER, customException.getErrorCode());
        verify(healthRepository, times(0)).findByMember(any());
        verify(memberRepository, times(1)).findById(any());
    }

    @Test
    void getHealthByMemberId_HealthNotFound() {
        Health health = mock(Health.class);
        Member requestMember = mock(Member.class);
        Member targetMember = mock(Member.class);
        List<Health> healthList = new ArrayList<>();
        healthList.add(health);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(targetMember));
        when(healthRepository.findByMember(requestMember)).thenReturn(Optional.empty());

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(requestMember));

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class, () -> healthService.getHealthByMemberId(1L));

        // Then
        Assertions.assertEquals(ErrorCode.NOT_FOUND_HEALTH, customException.getErrorCode());
        verify(healthRepository, times(1)).findByMember(any());
        verify(memberRepository, times(1)).findById(any());
    }
}