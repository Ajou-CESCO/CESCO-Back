package com.cesco.pillintime.plan.service;

import com.cesco.pillintime.api.cabinet.entity.Cabinet;
import com.cesco.pillintime.api.log.repository.LogRepository;
import com.cesco.pillintime.api.log.service.LogService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.dto.RequestPlanDto;
import com.cesco.pillintime.api.plan.dto.ResponsePlanDto;
import com.cesco.pillintime.api.plan.entity.Plan;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.api.plan.service.PlanService;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class PlanServiceTest {

    @Mock PlanRepository planRepository;
    @Mock MemberRepository memberRepository;
    @Mock LogRepository logRepository;
    @Mock LogService logService;
    @Mock SecurityUtil securityUtil;
    @InjectMocks
    private PlanService planService;

    private Member guardian;
    private Member patient;
    private Plan plan;

    @BeforeEach
    void setUp() {
        guardian = new Member();
        guardian.setId(1L);
        guardian.setName("guardian");
        guardian.setSsn("123123-1");
        guardian.setPhone("010-1234-1234");
        guardian.setManager(true);

        patient = new Member();
        patient.setId(2L);
        patient.setName("patient");
        patient.setSsn("789789-2");
        patient.setPhone("010-5678-5678");
        patient.setManager(false);

        plan = new Plan();
        plan.setId(1L);
        plan.setMember(patient);
        plan.setMedicineId("med-001");
        plan.setMedicineName("Medicine A");
        plan.setWeekday(7);
        plan.setTime(LocalTime.of(18, 0));
        plan.setStartAt(LocalDate.of(2024, 6, 2));
        plan.setEndAt(LocalDate.of(2024, 7, 7));
        plan.setGroupId(1L);
        plan.setMedicineSeries("asdf");
        plan.setCabinetIndex(1);


        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(authentication.getName()).thenReturn("guardian");
        lenient().when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
    }
    @Nested
    class 복약계획생성 {
        @Test
        void 복약계획생성() {
            // Given
            RequestPlanDto requestPlanDto = new RequestPlanDto();
            requestPlanDto.setMemberId(patient.getId());
            requestPlanDto.setMedicineId("med-001");
            requestPlanDto.setMedicineName("Medicine");
            requestPlanDto.setWeekdayList(List.of(1, 3, 5));
            requestPlanDto.setTimeList(List.of(LocalTime.of(8, 0), LocalTime.of(12, 0)));

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(securityUtil.checkPermission(guardian, patient)).thenReturn(true);
            when(planRepository.findMaxGroupId()).thenReturn(null);
            when(planRepository.saveAll(anyList())).thenReturn(List.of(plan));

            // When
            planService.createPlan(requestPlanDto);
            // Then
            verify(memberRepository, times(1)).findById(patient.getId());
            verify(planRepository, times(1)).findMaxGroupId();
            verify(planRepository, times(1)).saveAll(anyList());
        }

        @Test
        void 잘못된_회원_ID() {
            // Given
            RequestPlanDto requestPlanDto = new RequestPlanDto();
            requestPlanDto.setMemberId(guardian.getId());

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(requestPlanDto.getMemberId())).thenReturn(Optional.empty());

            // When
            CustomException exception = Assertions.assertThrows(CustomException.class, () ->
                    planService.createPlan(requestPlanDto));
            // Then
            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());

            verify(memberRepository, times(1)).findById(anyLong());
            verify(planRepository, never()).findMaxGroupId();
            verify(planRepository, never()).saveAll(anyList());
            verify(logRepository, never()).deleteAll(anyList());
        }
    }

    @Nested
    class 복약계획조회 {
        @Test
        void 복약계획조회() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(securityUtil.checkPermission(guardian, patient)).thenReturn(true);

            // Mock repository response
            List<Plan> planList = Collections.singletonList(plan);
            when(planRepository.findByMember(patient)).thenReturn(Optional.of(planList));

            // When
            List<ResponsePlanDto> result = planService.getPlanByMemberId(patient.getId());

            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(plan.getMedicineId(), result.get(0).getMedicineId());
            assertEquals(plan.getMedicineName(), result.get(0).getMedicineName());

            verify(memberRepository, times(1)).findById(patient.getId());
            verify(planRepository, times(1)).findByMember(patient);
        }
        @Test
        void 잘못된_회원_ID() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.empty());

            // When, Then
            CustomException exception = assertThrows(CustomException.class, () ->
                    planService.getPlanByMemberId(patient.getId())
            );

            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());

            verify(memberRepository, times(1)).findById(patient.getId());
            verify(planRepository, never()).findByMember(any(Member.class));
        }
    }

    @Nested
    class 복약계획수정 {
        @Test
        void 복약계획수정() {
            // Given
            RequestPlanDto requestPlanDto = new RequestPlanDto();
            requestPlanDto.setMemberId(patient.getId());
            requestPlanDto.setGroupId(plan.getGroupId());
            requestPlanDto.setMedicineId("med-002");
            requestPlanDto.setMedicineName("Medicine B");
            requestPlanDto.setWeekdayList(List.of(2, 4, 6));
            requestPlanDto.setTimeList(List.of(LocalTime.of(9, 0), LocalTime.of(13, 0)));

            patient.setFcmToken("fcmtoken");
            patient.setCabinet(mock(Cabinet.class));

            List<Plan> existingPlans = new ArrayList<>();
            existingPlans.add(plan);

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(requestPlanDto.getMemberId())).thenReturn(Optional.of(patient));
            when(securityUtil.checkPermission(guardian, patient)).thenReturn(true);

            when(planRepository.findPlanByMemberIdAndGroupId(patient.getId(), requestPlanDto.getGroupId())).thenReturn(Optional.of(existingPlans));
            when(planRepository.saveAll(anyList())).thenReturn(List.of(plan));

            // When
            planService.updatePlanByGroupId(requestPlanDto);

            // Then
            verify(memberRepository, times(1)).findById(patient.getId());
            verify(planRepository, times(1)).findPlanByMemberIdAndGroupId(patient.getId(), plan.getGroupId());
            verify(planRepository, times(1)).saveAll(anyList());
        }

        @Test
        void 잘못된_회원_ID() {
            // Given
            RequestPlanDto requestPlanDto = new RequestPlanDto();
            requestPlanDto.setMemberId(patient.getId());
            requestPlanDto.setGroupId(1L);

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(requestPlanDto.getMemberId())).thenReturn(Optional.empty());

            // When
            CustomException exception = assertThrows(CustomException.class, () ->
                    planService.updatePlanByGroupId(requestPlanDto)
            );

            // Then
            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());

            verify(memberRepository, times(1)).findById(requestPlanDto.getMemberId());
            verify(planRepository, never()).findPlanByMemberIdAndGroupId(patient.getId(), requestPlanDto.getGroupId());
            verify(planRepository, never()).saveAll(anyList());
            verify(logRepository, never()).findPlannedLog(any(Member.class), anyLong());
            verify(logRepository, never()).deleteAll(anyList());
        }
    }

    @Nested
    class 복약계획삭제 {
        @Test
        void 복약계획삭제() {
            // Given
            Long groupId = plan.getGroupId();

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(logRepository.findPlannedLog(patient, groupId)).thenReturn(Optional.of(List.of()));
            when(planRepository.findTargetPlan(patient, groupId)).thenReturn(Optional.of(List.of()));

            // When
            assertDoesNotThrow(() -> planService.deletePlanById(patient.getId(), groupId));

            // Then
            verify(memberRepository, times(1)).findById(patient.getId());
            verify(logRepository, times(1)).findPlannedLog(patient, groupId);
            verify(logRepository, times(1)).deleteAll(anyList());
            verify(planRepository, times(1)).findTargetPlan(patient, groupId);
            verify(planRepository, times(1)).deleteAll(anyList());
        }
        @Test
        void 탈퇴한_회원_ID() {
            // Given
            Long groupId = plan.getGroupId();

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.empty());

            // When
            CustomException exception = assertThrows(CustomException.class, () ->
                    planService.deletePlanById(patient.getId(), groupId)
            );

            // Then
            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
            verify(memberRepository, times(1)).findById(patient.getId());
            verify(logRepository, times(0)).findPlannedLog(patient, groupId);
            verify(logRepository, times(0)).deleteAll(anyList());
            verify(planRepository, times(0)).findTargetPlan(patient, groupId);
            verify(planRepository, times(0)).deleteAll(anyList());
        }
        @Test
        void 이미_삭제한_복약기록() {
            // Given
            Long groupId = plan.getGroupId();

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(logRepository.findPlannedLog(patient, groupId)).thenReturn(Optional.of(List.of()));
            when(planRepository.findTargetPlan(patient, groupId)).thenReturn(Optional.empty());

            patient.setFcmToken("fcmtoken");
            patient.setCabinet(mock(Cabinet.class));
            // When
            planService.deletePlanById(patient.getId(), groupId);

            // Then
//            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
            verify(memberRepository, times(1)).findById(patient.getId());
            verify(logRepository, times(1)).findPlannedLog(patient, groupId);
            verify(logRepository, times(1)).deleteAll(anyList());
            verify(planRepository, times(1)).findTargetPlan(patient, groupId);
            verify(planRepository, times(0)).deleteAll(anyList());
        }
    }
}