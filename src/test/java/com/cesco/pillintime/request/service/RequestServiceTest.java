package com.cesco.pillintime.request.service;

import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.service.MemberServiceTest;
import com.cesco.pillintime.request.dto.RequestDto;
import com.cesco.pillintime.request.entity.Request;
import com.cesco.pillintime.request.mapper.RequestMapper;
import com.cesco.pillintime.request.repository.RequestRepository;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RequestServiceTest {

    private RequestRepository requestRepository;
    private RequestService requestService;
    public static Request createRequest() {
        long longValue = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        Request request = new Request();
        request.setSender(MemberServiceTest.createMember());
        request.setReceiverPhone("010"+"-"+String.format("%04d", longValue % 10000)+"-"+String.format("%04d", longValue*7 % 10000));
        return request;
    }

    @BeforeEach
    void init() {
        requestRepository = mock(RequestRepository.class);
        requestService = new RequestService(requestRepository);
    }

    @Test
    void createRequest_Success() {
        // Given
        Member member = MemberServiceTest.createMember();
        Request request = createRequest();
        RequestDto requestDto = RequestMapper.INSTANCE.toDto(request);

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        // When
        requestService.createRequest(requestDto);

        // Then
        verify(requestRepository,times(1)).save(any());
    }

    @Test
    void getRelatedRequest_Success() {
        // Given
        Member member = MemberServiceTest.createMember();
        Request request = createRequest();
        List<Request> requestList = new ArrayList<>();
        List<RequestDto> requestDtoList = new ArrayList<>();
        requestDtoList.add(RequestMapper.INSTANCE.toDto(request));
        requestList.add(request);

        when(requestRepository.findByReceiverPhone(member.getPhone())).thenReturn(Optional.of(requestList));

        // SecurityUtil.getCurrentMember..정상 동작 코드
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("username");
        when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
        when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(member));

        // When
        List<RequestDto> returnRequestDto = requestService.getRelatedRequest();

        // Then
        Assertions.assertEquals(requestDtoList,returnRequestDto);
        verify(requestRepository,times(1)).findByReceiverPhone(any());
    }

    @Test
    void deleteRequestById() {
        // Given
        // When
        requestService.deleteRequestById(1L);
        // Then
        verify(requestRepository,times(1)).deleteById(any());
    }
}