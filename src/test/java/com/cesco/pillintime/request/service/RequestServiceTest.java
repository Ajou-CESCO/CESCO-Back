package com.cesco.pillintime.request.service;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.relation.repository.RelationRepository;
import com.cesco.pillintime.api.request.dto.RequestDto;
import com.cesco.pillintime.api.request.entity.Request;
import com.cesco.pillintime.api.request.mapper.RequestMapper;
import com.cesco.pillintime.api.request.repository.RequestRepository;
import com.cesco.pillintime.api.request.service.RequestService;
import com.cesco.pillintime.fcm.strategy.FcmStrategy;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    private RelationRepository relationRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ApplicationContext context;

    @InjectMocks
    private RequestService requestService;

    private Member guardian;
    private Member patient;
    private Request request;
    @BeforeEach
    void init() {
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

        request = new Request();
        request.setId(1L);
        request.setSender(guardian);
        request.setReceiverPhone(patient.getPhone());

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(authentication.getName()).thenReturn("guardian");
        lenient().when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));

    }

    @Nested
    class 요청생성 {
        @Test
        void 새로운_요청_생성() {
            // Given
            RequestDto requestDto = RequestMapper.INSTANCE.toDto(request);

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(requestRepository.findBySenderAndReceiverPhone(guardian, patient.getPhone())).thenReturn(Optional.empty());
            when(memberRepository.findByPhone(patient.getPhone())).thenReturn(Optional.of(patient));
            when(relationRepository.findByManagerAndReceiverPhone(guardian, requestDto.getReceiverPhone())).thenReturn(Optional.empty());
            when(requestRepository.save(any(Request.class))).thenReturn(request);

            FcmStrategy fcmStrategy = mock(FcmStrategy.class);
            lenient().when(context.getBean("requestStrategy", FcmStrategy.class)).thenReturn(fcmStrategy);

            // When
            Request result = requestService.createRequest(requestDto);

            // Then
            assertNotNull(result);
            assertEquals(guardian, result.getSender());
            assertEquals(patient.getPhone(), result.getReceiverPhone());

            verify(relationRepository, times(1)).findByManagerAndReceiverPhone(guardian, requestDto.getReceiverPhone());
            verify(requestRepository, times(1)).findBySenderAndReceiverPhone(guardian, patient.getPhone());
            verify(memberRepository, times(1)).findByPhone(patient.getPhone());
            verify(fcmStrategy, times(1)).execute(anyMap());
        }

        @Test
        void 기존_요청_재사용() {
            // Given
            RequestDto requestDto = RequestMapper.INSTANCE.toDto(request);

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(requestRepository.findBySenderAndReceiverPhone(guardian, requestDto.getReceiverPhone())).thenReturn(Optional.of(request));
            when(relationRepository.findByManagerAndReceiverPhone(guardian, requestDto.getReceiverPhone())).thenReturn(Optional.empty());

            FcmStrategy fcmStrategy = mock(FcmStrategy.class);
            lenient().when(context.getBean("requestStrategy", FcmStrategy.class)).thenReturn(fcmStrategy);

            // When
            Request result = requestService.createRequest(requestDto);

            // Then
            assertNotNull(result);
            assertEquals(request.getId(), result.getId());
            assertEquals(guardian, result.getSender());
            assertEquals(patient.getPhone(), result.getReceiverPhone());

            verify(relationRepository, times(1)).findByManagerAndReceiverPhone(guardian, requestDto.getReceiverPhone());
            verify(requestRepository, times(1)).findBySenderAndReceiverPhone(guardian, patient.getPhone());
            verify(requestRepository, never()).save(any(Request.class));
            verify(memberRepository, times(1)).findByPhone(patient.getPhone());
            verify(fcmStrategy, never()).execute(anyMap());
        }

    }
    @Nested
    class 요청조회 {

        @Test
        void 요청조회() {
            RequestDto requestDto = RequestMapper.INSTANCE.toDto(request);
            // Mock Repository 동작 설정
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(requestRepository.findByReceiverPhone(anyString())).thenReturn(Optional.of(List.of(request)));

            // When
            List<RequestDto> requestDtoList = requestService.getRelatedRequest();

            // Then
            assertEquals(List.of(requestDto),requestDtoList);
            verify(requestRepository, times(1)).findByReceiverPhone(guardian.getPhone());
        }
        @Test
        void 예외_조회기록없음() {
            RequestDto requestDto = RequestMapper.INSTANCE.toDto(request);
            // Mock Repository 동작 설정
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(requestRepository.findByReceiverPhone(anyString())).thenReturn(Optional.of(Collections.emptyList()));

            // When
            List<RequestDto> requestDtoList = requestService.getRelatedRequest();

            // Then
            assertEquals(Collections.emptyList(),requestDtoList);
            verify(requestRepository, times(1)).findByReceiverPhone(guardian.getPhone());
        }
    }
    @Nested
    class 요청삭제 {
        @Test
        void 요청삭제() {
            // Given
            // When
            requestService.deleteRequestById(request.getId());
            // Then
            verify(requestRepository, times(1)).deleteById(request.getId());
        }
        // 해당 id가 없으면 아무런 동작을 수행하지 않기에 테스트 미생성.
    }
    @ExtendWith(TestNamePrinter.class)
    static class TestNamePrinter implements TestWatcher {

        @Override
        public void testSuccessful(ExtensionContext context) {
            System.out.println("Test completed successfully: " + context.getDisplayName());
        }

        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            System.out.println("Test failed: " + context.getDisplayName() + ", Reason: " + cause.getMessage());
        }

        @Override
        public void testAborted(ExtensionContext context, Throwable cause) {
            System.out.println("Test aborted: " + context.getDisplayName() + ", Reason: " + cause.getMessage());
        }
    }
}