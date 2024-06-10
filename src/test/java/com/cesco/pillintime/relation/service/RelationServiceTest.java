package com.cesco.pillintime.relation.service;

import com.cesco.pillintime.CustomTestWatcher;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.relation.dto.RelationDto;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.api.relation.repository.RelationRepository;
import com.cesco.pillintime.api.relation.service.RelationService;
import com.cesco.pillintime.api.request.entity.Request;
import com.cesco.pillintime.api.request.repository.RequestRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.fcm.strategy.FcmStrategy;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.security.SecurityUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@ExtendWith(CustomTestWatcher.class)
class RelationServiceTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RelationRepository relationRepository;

    @Mock
    private ApplicationContext context;

    @InjectMocks
    private RelationService relationService;

    private Member guardian;
    private Member patient;
    private Request request;
    private Relation relation;

    @BeforeEach
    void setUp() {
        guardian = new Member();
        guardian.setId(1L);
        guardian.setName("guardian");
        guardian.setSsn("123123-1");
        guardian.setPhone("010-1234-1234");
        guardian.setManager(true);
        guardian.setSubscriber(true);

        patient = new Member();
        patient.setId(2L);
        patient.setName("patient");
        patient.setSsn("789789-2");
        patient.setPhone("010-5678-5678");
        patient.setManager(false);
        patient.setSubscriber(true);

        request = new Request();
        request.setId(1L);
        request.setSender(guardian);
        request.setReceiverPhone(patient.getPhone());

        relation = new Relation();
        relation.setId(1L);
        relation.setClient(patient);
        relation.setManager(guardian);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(authentication.getName()).thenReturn("guardian");
        lenient().when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
    }
    @Nested
    class 관계생성 {
        @Test
        void 관계생성() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.ofNullable(patient));
            when(requestRepository.findById(request.getId())).thenReturn(Optional.ofNullable(request));

            FcmStrategy fcmStrategy = mock(FcmStrategy.class);
            lenient().when(context.getBean("relationCreatedStrategy", FcmStrategy.class)).thenReturn(fcmStrategy);

            // When
            relationService.createRelation(request.getId());

            // Then
            verify(requestRepository, times(1)).findById(request.getId());
            verify(relationRepository, times(1)).save(any());
            verify(requestRepository, times(1)).delete(request);
            verify(fcmStrategy, times(1)).execute(anyMap());
        }
        @Test
        void 예외_요청ID확인불가() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.ofNullable(patient));
            when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

            FcmStrategy fcmStrategy = mock(FcmStrategy.class);
            lenient().when(context.getBean("relationCreatedStrategy", FcmStrategy.class)).thenReturn(fcmStrategy);

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                relationService.createRelation(request.getId());
            });

            // Then
            assertEquals(ErrorCode.NOT_FOUND_REQUEST, exception.getErrorCode());
            verify(requestRepository, times(1)).findById(request.getId());
            verify(relationRepository, times(0)).save(any());
            verify(requestRepository, times(0)).delete(request);
            verify(fcmStrategy, times(0)).execute(anyMap());
        }
    }

    @Nested
    class 관계조회 {
        /**
         * 관계가 형성되지 없는 상태에서 관계를 조회했을 경우
         */
        @Test
        void 미관계조회() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.ofNullable(guardian));
            when(relationRepository.findByMember(guardian)).thenReturn(Optional.empty());

            FcmStrategy fcmStrategy = mock(FcmStrategy.class);

            // When
            List<RelationDto> relationList = relationService.getRelationList();

            // Then
            Assertions.assertNull(relationList);
            verify(relationRepository, times(1)).findByMember(guardian);
            verify(fcmStrategy, times(0)).execute(anyMap());
        }

        /**
         * 관계가 형성된 상태에서 관계를 조회했을 경우
         */
        @Test
        void 보호자가_관계조회() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(relationRepository.findByMember(guardian)).thenReturn(Optional.of(List.of(relation)));

            // When
            List<RelationDto> relationDtoList = relationService.getRelationList();

            // Then
            assertNotNull(relationDtoList);
            verify(relationRepository, times(1)).findByMember(guardian);

            assertEquals(1, relationDtoList.size());
            RelationDto relationDto = relationDtoList.get(0);
            assertEquals(patient.getId(), relationDto.getMemberId());
            assertEquals(patient.getName(), relationDto.getMemberName());
            assertEquals(patient.getPhone(), relationDto.getMemberPhone());
            assertEquals(patient.getSsn(), relationDto.getMemberSsn());
        }
        @Test
        void 피보호자가_관계조회() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(patient));
            when(relationRepository.findByMember(patient)).thenReturn(Optional.of(List.of(relation)));

            // When
            List<RelationDto> relationDtoList = relationService.getRelationList();

            // Then
            assertNotNull(relationDtoList);
            verify(relationRepository, times(1)).findByMember(patient);

            assertEquals(1, relationDtoList.size());
            RelationDto relationDto = relationDtoList.get(0);
            assertEquals(guardian.getId(), relationDto.getMemberId());
            assertEquals(guardian.getName(), relationDto.getMemberName());
            assertEquals(guardian.getPhone(), relationDto.getMemberPhone());
            assertEquals(guardian.getSsn(), relationDto.getMemberSsn());
        }


    }

    @Nested
    class 관계삭제 {
        /**
         * 관계 삭제
         */
        @Test
        void 보호자_관계삭제() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.ofNullable(guardian));
            when(relationRepository.findById(relation.getId())).thenReturn(Optional.ofNullable(relation));

            FcmStrategy fcmStrategy = mock(FcmStrategy.class);
            lenient().when(context.getBean("relationDeletedStrategy", FcmStrategy.class)).thenReturn(fcmStrategy);

            // When
            relationService.deleteRelation(relation.getId());

            // Then
            verify(relationRepository, times(1)).findById(relation.getId());
            verify(fcmStrategy, times(1)).execute(anyMap());
        }
        @Test
        void 피보호자_관계삭제() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.ofNullable(patient));
            when(relationRepository.findById(relation.getId())).thenReturn(Optional.ofNullable(relation));

            FcmStrategy fcmStrategy = mock(FcmStrategy.class);
            lenient().when(context.getBean("relationDeletedStrategy", FcmStrategy.class)).thenReturn(fcmStrategy);

            // When
            relationService.deleteRelation(relation.getId());

            // Then
            verify(relationRepository, times(1)).findById(relation.getId());
            verify(fcmStrategy, times(1)).execute(anyMap());
        }
        /**
         * 관계 조회 실패 - 미관계
         */
        @Test
        void 예외_미관계() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.ofNullable(guardian));
            when(relationRepository.findById(relation.getId())).thenReturn(Optional.empty());

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                relationService.deleteRelation(relation.getId());
            });

            // Then
            Assertions.assertEquals(ErrorCode.NOT_FOUND_RELATION, exception.getErrorCode());
            verify(relationRepository, times(1)).findById(relation.getId());
        }
    }
}