package com.cesco.pillintime.request.service;

import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.request.dto.RequestDto;
import com.cesco.pillintime.request.repository.RequestRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequestServiceTest {

    private RequestRepository requestRepository;
    private MemberRepository memberRepository;
    private RequestService requestService;

    public static RequestDto createRequestDto() {
        long longValue = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        RequestDto requestDto = new RequestDto();
        requestDto.setSenderId(1L);
        requestDto.setReceiverPhone("010"+"-"+String.format("%04d", longValue % 10000)+"-"+String.format("%04d", longValue*7 % 10000));
        return requestDto;
    }


    @BeforeEach
    void init() {
        requestRepository = mock(RequestRepository.class);
        requestService = new RequestService(requestRepository,memberRepository);
    }

    @Test
    void createRequest_Success() {
        // Given
        RequestDto requestDto = createRequestDto();

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMemberId()).thenReturn(1L);

        // When
        requestService.createRequest(requestDto);

        // Then
        verify(requestRepository,times(1)).save(any());
    }

    @Test
    void getRelatedRequest() {

    }

    @Test
    void deleteRequestById() {

    }
}