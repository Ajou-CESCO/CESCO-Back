package com.cesco.pillintime.log.service;

import com.cesco.pillintime.api.cabinet.entity.Cabinet;
import com.cesco.pillintime.api.cabinet.repository.CabinetRepository;
import com.cesco.pillintime.api.log.dto.LogDto;
import com.cesco.pillintime.api.log.dto.SensorDto;
import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.log.entity.TakenStatus;
import com.cesco.pillintime.api.log.repository.LogRepository;
import com.cesco.pillintime.api.log.service.LogService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.entity.Plan;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    private LogRepository logRepository;
    @Mock
    private PlanRepository planRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CabinetRepository cabinetRepository;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private ApplicationContext context;
    @InjectMocks
    private LogService logService;

    private Member guardian;
    private Member patient;
    private Plan plan;
    private Cabinet cabinet;
    private SensorDto sensorDto;
    private Log log;

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
        plan.setWeekday(7); // 2024-06-10은 일요일, weekday는 7 (1-월요일, ..., 7-일요일)
        plan.setTime(LocalTime.of(18, 0)); // 예시로 정오에 복용 예정
        plan.setEndAt(LocalDate.now().plusWeeks(4)); // 4주 후 종료 예정

        cabinet = new Cabinet();
        cabinet.setId(1L);
        cabinet.setSerial("TEST_SERIAL");

        sensorDto = new SensorDto();
        sensorDto.setSerial("serial123");
        sensorDto.setIndex(1);

        log = new Log();
        log.setMember(patient);
        log.setTakenStatus(TakenStatus.NOT_COMPLETED);

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(authentication.getName()).thenReturn("guardian");
        lenient().when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
    }


    @Nested
    class 복용기록생성 {
        @Test
        void 복용기록_생성() {
            LocalDate today = LocalDate.now();

            when(planRepository.findActivePlan(eq(today))).thenReturn(Optional.of(List.of(plan)));
            when(logRepository.existsByMemberAndPlanAndPlannedAt(any(Member.class), any(Plan.class), any(LocalDateTime.class))).thenReturn(false);

            // 테스트 메서드 호출
            logService.createDoseLog();

            // logRepository.save 메서드가 한 번 호출되었는지 검증
            verify(logRepository, times(1)).save(any(Log.class));
            verify(planRepository, times(1)).findActivePlan(eq(today));
        }
        @Test
        void 무복용기록_미생성() {
            when(planRepository.findActivePlan(any(LocalDate.class))).thenReturn(Optional.empty());

            // When
            logService.createDoseLog();

            // Then
            verify(logRepository, never()).save(any(Log.class));
        }
        @Test
        void 복용기록_중복_미생성() {
            // Given
            when(planRepository.findActivePlan(any(LocalDate.class))).thenReturn(Optional.of(List.of(plan)));
            when(logRepository.existsByMemberAndPlanAndPlannedAt(any(Member.class), any(Plan.class), any(LocalDateTime.class))).thenReturn(true);

            // When
            logService.createDoseLog();

            // Then
            verify(logRepository, never()).save(any(Log.class));
        }
    }

    @Nested
    class 복용기록조회 {
        @Test
        void 피보호자가_복용기록조회() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(patient));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

            List<Log> logList = new ArrayList<>();
            logList.add(log);

            when(logRepository.findByMemberAndPlannedAtBetween(eq(patient), eq(startOfDay), eq(endOfDay))).thenReturn(Optional.of(logList));

            // When
            List<LogDto> result = logService.getDoseLogByMemberId(patient.getId());

            // Then
            LogDto logDto = result.get(0);
            assertEquals(log.getId(), logDto.getId());
            assertEquals(log.getMember().getId(), logDto.getMemberId());
            assertEquals(log.getPlannedAt(), logDto.getPlannedAt());
        }
        @Test
        void 피보호자의_복용기록조회() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.of(patient));
            when(securityUtil.checkPermission(guardian, patient)).thenReturn(true);

            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

            List<Log> logList = new ArrayList<>();
            logList.add(log);

            when(logRepository.findByMemberAndPlannedAtBetween(eq(patient), eq(startOfDay), eq(endOfDay))).thenReturn(Optional.of(logList));

            // When
            List<LogDto> result = logService.getDoseLogByMemberId(patient.getId());

            // Then
            LogDto logDto = result.get(0);
            assertEquals(log.getId(), logDto.getId());
            assertEquals(log.getMember().getId(), logDto.getMemberId());
            assertEquals(log.getPlannedAt(), logDto.getPlannedAt());
        }
        @Test
        void 잘못된_피보호자ID() {
            // Given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            CustomException exception = assertThrows(CustomException.class, () ->
                    logService.getDoseLogByMemberId(patient.getId())
            );
            // Then
            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());
            verify(memberRepository,times(1)).findById(anyLong());
        }
        @Test
        void 피보호자가_보호자인_경우 () {
            // Given
            patient.setManager(true);
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(patient.getId())).thenReturn(Optional.of(patient));

            // When
            CustomException exception = assertThrows(CustomException.class, () ->
                    logService.getDoseLogByMemberId(patient.getId())
            );

            // Then
            assertEquals(ErrorCode.INVALID_USERTYPE, exception.getErrorCode());
            verify(memberRepository,times(1)).findById(anyLong());
        }
    }

    @Nested
    class 복용기록업데이트 {
        @Test
        void 복용기록업데이트() {
            // Given

            when(cabinetRepository.findBySerial(eq("serial123"))).thenReturn(Optional.of(cabinet));
            when(memberRepository.findByCabinet(any(Cabinet.class))).thenReturn(Optional.of(patient));
            when(logRepository.findTargetLog(eq(patient), eq(sensorDto.getIndex()), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(Optional.of(log));

            // When
            logService.updateDoseLogByCabinet(sensorDto);

            // Then
            verify(cabinetRepository, times(1)).findBySerial("serial123");
            verify(memberRepository, times(1)).findByCabinet(cabinet);
            verify(logRepository, times(1)).findTargetLog(eq(patient), eq(sensorDto.getIndex()), any(LocalDateTime.class), any(LocalDateTime.class));
            verify(logRepository, times(1)).save(log);

            assertEquals(TakenStatus.COMPLETED, log.getTakenStatus());
        }
        @Test
        void 예외_약통없음() {
            // Given
            sensorDto.setIndex(1);

            // CabinetRepository mock 설정
            when(cabinetRepository.findBySerial(any(String.class))).thenReturn(Optional.empty());

            // When, Then
            CustomException exception = assertThrows(CustomException.class, () -> {
                logService.updateDoseLogByCabinet(sensorDto);
            });

            assertEquals(ErrorCode.NOT_FOUND_CABINET, exception.getErrorCode());
        }
    }

    @Nested
    class 복용알림 {
        @Test
        void 복용알림_현재시간() {
            // Given
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime startOfSecond = currentTime.truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime endOfSecond = startOfSecond.plus(999, ChronoUnit.MILLIS);

            FcmStrategy clientPlanStrategy = mock(FcmStrategy.class);
            lenient().when(context.getBean("clientPlanStrategy", FcmStrategy.class)).thenReturn(clientPlanStrategy);

            when(logRepository.findPlannedLog(eq(startOfSecond), eq(endOfSecond))).thenReturn(Optional.of(List.of(log)));

            // When
            logService.updateDoseLogByCurrentTime();

            // Then
            verify(logRepository, times(1)).findPlannedLog(eq(startOfSecond), eq(endOfSecond));
            verify(clientPlanStrategy, times(1)).execute(any(Map.class));
        }
        @Test
        void 복욕알림_30분초과() {
            // Given
            log.setTakenStatus(TakenStatus.NOT_COMPLETED);

            LocalDateTime currentTime = LocalDateTime.of(2024, 6, 9, 19, 0);
            LocalDateTime targetTime = currentTime.minusMinutes(30);

            // Mocking FcmStrategy beans
            FcmStrategy clientLogStrategy = mock(FcmStrategy.class);
            when(context.getBean("clientOverLogStrategy", FcmStrategy.class)).thenReturn(clientLogStrategy);

            FcmStrategy managerLogStrategy = mock(FcmStrategy.class);
            when(context.getBean("managerOverLogStrategy", FcmStrategy.class)).thenReturn(managerLogStrategy);

            when(logRepository.findIncompleteLog(any(LocalDateTime.class))).thenReturn(List.of(log));

            // When
            logService.updateDoseLogByCurrentTime();

            // Then
            verify(logRepository, times(1)).findIncompleteLog(any(LocalDateTime.class));
            assertEquals(TakenStatus.TIMED_OUT, log.getTakenStatus());
            verify(logRepository, times(1)).save(log);
            verify(clientLogStrategy, times(1)).execute(any(Map.class));
            verify(managerLogStrategy, times(1)).execute(any(Map.class));
        }
    }
}