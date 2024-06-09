package com.cesco.pillintime.health.service;

import com.cesco.pillintime.api.health.dto.HealthDto;
import com.cesco.pillintime.api.health.entity.Health;
import com.cesco.pillintime.api.health.mapper.HealthMapperImpl;
import com.cesco.pillintime.api.health.repository.HealthRepository;
import com.cesco.pillintime.api.health.service.HealthService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.security.SecurityUtil;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class HealthServiceTest {

    @Mock
    private HealthRepository healthRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private HealthService healthService;

    private Member guardian;
    private Member patient;
    private Health health;

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

        health = new Health();
        health.setId(1L);
        health.setMember(patient);
        health.setSteps(8000L);
        health.setCalorie(2500L);
        health.setHeartRate(70L);
        health.setSleepTime(8L);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(authentication.getName()).thenReturn("guardian");
        lenient().when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
    }

    @Nested
    class 건강데이터생성 {
        @Test
        void 피보호자_건강데이터생성() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(patient));
            doReturn(health).when(healthRepository).save(any(Health.class));

            HealthDto healthDto = HealthMapperImpl.INSTANCE.toDto(health);

            // when
            healthService.createHealth(healthDto);

            // then
            verify(healthRepository, times(1)).save(any(Health.class)); // 수정된 부분
        }

        @Test
        void 보호자_건강데이터생성() {
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(patient));
            HealthDto healthDto = HealthMapperImpl.INSTANCE.toDto(health);

            healthService.createHealth(healthDto);
        }
    }

    @Nested
    class 건강데이터조회 {
        @Test
        void 피보호자_건강데이터조회() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(patient));
                // 첫 번째 값은 고정으로 두 번째 값은 타입만 같으면 된다.
            when(healthRepository.findRecentHealthByDate(eq(patient), any(LocalDate.class))).thenReturn(Optional.of(health));

            // when
            HealthDto result = healthService.getHealthByMemberId(null);

            // then
            assertNotNull(result);
            assertEquals(8000L, result.getSteps());
            assertEquals(2500L, result.getCalorie());
            assertEquals(70L, result.getHeartRate());
            assertEquals(8L, result.getSleepTime());
            assertEquals(40L, result.getAgeGroup());
            verify(healthRepository, times(1)).findRecentHealthByDate(eq(patient), any(LocalDate.class));
        }
        @Test
        void 보호자_건강데이터조회() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(securityUtil.checkPermission(guardian, patient)).thenReturn(true);
            when(healthRepository.findRecentHealthByDate(eq(patient), any(LocalDate.class))).thenReturn(Optional.of(health));

            // when
            HealthDto result = healthService.getHealthByMemberId(patient.getId());

            // then
            assertNotNull(result);
            assertEquals(8000L, result.getSteps());
            assertEquals(2500L, result.getCalorie());
            assertEquals(70L, result.getHeartRate());
            assertEquals(8L, result.getSleepTime());
            assertEquals(40L, result.getAgeGroup());
            verify(memberRepository, times(1)).findById(patient.getId());
            verify(securityUtil, times(1)).checkPermission(guardian, patient);
            verify(healthRepository, times(1)).findRecentHealthByDate(eq(patient), any(LocalDate.class));
        }
        @Test
        void 예외_잘못된_피보호자ID() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.empty());
            lenient().when(securityUtil.checkPermission(guardian, patient)).thenReturn(false);
            lenient().when(healthRepository.findRecentHealthByDate(eq(patient), any(LocalDate.class))).thenReturn(Optional.of(health));

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                healthService.getHealthByMemberId(patient.getId());
            });
            // then
            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
            verify(memberRepository, times(1)).findById(patient.getId());
            verify(securityUtil, times(0)).checkPermission(guardian, patient);
            verify(healthRepository, times(0)).findRecentHealthByDate(eq(patient), any(LocalDate.class));
        }
    }
}