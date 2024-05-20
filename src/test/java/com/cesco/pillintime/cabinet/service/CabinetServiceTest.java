package com.cesco.pillintime.cabinet.service;

import com.cesco.pillintime.cabinet.dto.CabinetDto;
import com.cesco.pillintime.cabinet.entity.Cabinet;
import com.cesco.pillintime.cabinet.repository.CabinetRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.util.SecurityUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CabinetServiceTest {

    private CabinetRepository cabinetRepository;
    private MemberRepository memberRepository;
    private CabinetService cabinetService;

    @BeforeEach
    void init() {
        memberRepository = mock(MemberRepository.class);
        cabinetRepository = mock(CabinetRepository.class);
        cabinetService = new CabinetService(cabinetRepository, memberRepository);
    }

    @Test
    void createCabinet_Success_Null() {
        // Given
        Cabinet cabinet = mock(Cabinet.class);
        CabinetDto cabinetDto = new CabinetDto();
        Member member = mock(Member.class);
        cabinetDto.setSerial(cabinet.getSerial());
        cabinetDto.setOwnerId(null);

        when(cabinetRepository.findBySerial(any())).thenReturn(Optional.of(cabinet));

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        // When
        cabinetService.createCabinet(cabinetDto);

        // Then
        verify(memberRepository, times(0)).findById(any());
        verify(cabinetRepository, times(1)).save(any());
        verify(cabinetRepository, times(1)).findBySerial(any());
    }

    @Test
    void createCabinet_Success_1L() {
        // Given
        Cabinet cabinet = mock(Cabinet.class);
        CabinetDto cabinetDto = new CabinetDto();
        Member member = mock(Member.class);
        cabinetDto.setSerial(cabinet.getSerial());
        cabinetDto.setOwnerId(1L);

        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(cabinetRepository.findBySerial(any())).thenReturn(Optional.of(cabinet));

        // When
        cabinetService.createCabinet(cabinetDto);

        // Then
        verify(memberRepository, times(1)).findById(any());
        verify(cabinetRepository, times(1)).save(any());
        verify(cabinetRepository, times(1)).findBySerial(any());
    }

    @Test
    void createCabinet_NotFoundMember() {
        // Given
        Cabinet cabinet = mock(Cabinet.class);
        CabinetDto cabinetDto = new CabinetDto();
        Member member = mock(Member.class);
        cabinetDto.setSerial(cabinet.getSerial());
        cabinetDto.setOwnerId(1L);

        when(memberRepository.findById(any())).thenReturn(Optional.empty());
        when(cabinetRepository.findBySerial(any())).thenReturn(Optional.of(cabinet));

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        // When
        CustomException customException =Assertions.assertThrows(CustomException.class,
                () -> cabinetService.createCabinet(cabinetDto));
        // Then
        Assertions.assertEquals(ErrorCode.NOT_FOUND_USER, customException.getErrorCode());
        verify(memberRepository, times(1)).findById(any());
        verify(cabinetRepository, times(0)).save(any());
        verify(cabinetRepository, times(0)).findBySerial(any());
    }

    @Test
    void createCabinet_NotFoundCabinet() {
        // Given
        Cabinet cabinet = mock(Cabinet.class);
        CabinetDto cabinetDto = new CabinetDto();
        Member member = mock(Member.class);
        cabinetDto.setSerial(cabinet.getSerial());
        cabinetDto.setOwnerId(1L);

        when(memberRepository.findById(any())).thenReturn(Optional.of(member));
        when(cabinetRepository.findBySerial(any())).thenReturn(Optional.empty());

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        // When
        CustomException customException = Assertions.assertThrows(CustomException.class,
                () -> cabinetService.createCabinet(cabinetDto));

        // Then
        Assertions.assertEquals(ErrorCode.NOT_FOUND_CABINET, customException.getErrorCode());
        verify(memberRepository, times(1)).findById(any());
        verify(cabinetRepository, times(1)).findBySerial(any());
        verify(cabinetRepository, times(0)).save(any());
    }

    @Test
    void getSensorData() {

    }
}