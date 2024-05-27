package com.cesco.pillintime.api.log.service;

import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.log.mapper.LogMapper;
import com.cesco.pillintime.api.log.repository.LogRepository;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.api.relation.repository.RelationRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.log.dto.LogDto;
import com.cesco.pillintime.api.log.entity.TakenStatus;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.entity.Plan;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.service.FcmService;
import com.cesco.pillintime.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final RelationRepository relationRepository;
    private final FcmService fcmService;
    private final SecurityUtil securityUtil;

    @Scheduled(cron = "0 50 23 * * SUN")
    @Transactional
    public void createDoseLog() {
        LocalDate today = LocalDate.now();

        planRepository.findActivePlan(today).ifPresent(planList -> {
            for (Plan plan : planList) {
                LocalDate plannedDate = calculateNextPlannedDate(today, plan.getWeekday());
                LocalTime plannedTime = plan.getTime();
                LocalDateTime plannedAt = plannedDate.atTime(plannedTime);

                LocalDate endAt = plan.getEndAt();

                // 해당 날짜 및 Plan 에 대한 Log 가 없을 경우에만 생성
                // 계산된 plannedAt이 계획의 종료일보다 작거나 같을 경우에만 생성
                boolean logExists = logRepository.existsByMemberAndPlanAndPlannedAt(plan.getMember(), plan, plannedAt);
                if (!logExists && plannedDate.isBefore(endAt) || plannedDate.isEqual(endAt)) {
                    Log log = new Log();
                    log.setMember(plan.getMember());
                    log.setPlan(plan);
                    log.setPlannedAt(plannedAt);
                    log.setTakenStatus(TakenStatus.NOT_COMPLETED);

                    System.out.println(log.getPlannedAt());

                    logRepository.save(log);
                }
            }
        });
    }

    public List<LogDto> getDoseLogByMemberId(Long targetId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        if (targetMember.isManager()) {
            throw new CustomException(ErrorCode.INVALID_USERTYPE);
        }

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        Optional<List<Log>> logListOptional = logRepository.findByMemberAndPlannedAtBetween(targetMember, startOfDay, endOfDay);

        List<LogDto> logDtoList = new ArrayList<>();
        logListOptional.ifPresent(logs -> {
            for (Log log : logs) {
                System.out.println(log.getPlannedAt());
                LogDto logDto = LogMapper.INSTANCE.toDto(log);
                System.out.println(logDto.getPlannedAt());
                logDtoList.add(logDto);
            }
        });

        return logDtoList;
    }

    @Scheduled(cron = "0 1/31 * * * *")
    public void updateDoseLogByCurrentTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime targetTime = currentTime.minusMinutes(30);

        // 예정 시각보다 30분 초과한 미완료된 로그들을 조회하여 업데이트
        List<Log> incompletedLogList = logRepository.findIncompleteLog(targetTime);
        incompletedLogList.forEach(log -> {
            log.setTakenStatus(TakenStatus.TIMED_OUT);
            logRepository.save(log);

            Member client = log.getMember();
            FcmRequestDto fcmRequestDto = new FcmRequestDto(
                    client.getId(),
                    "[약속시간] \uD83D\uDEA8 알림 \uD83D\uDEA8",
                    "‼\uFE0F " + log.getPlannedAt() + " 에 " + log.getPlan().getCabinetIndex() + "번째 칸의 약을 먹지 않았어요."
            );

            try {
                fcmService.sendPushAlarm(fcmRequestDto);
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FCM_SERVER_ERROR);
            }

            relationRepository.findByMember(client)
                    .ifPresent((list) -> {
                        for (Relation relation : list) {
                            Member manager = relation.getManager();

                            fcmRequestDto.setTargetId(manager.getId());
                            fcmRequestDto.setBody(log.getMember().getName() + " 님이 " + log.getPlannedAt() + " 에 "
                            + log.getPlan().getCabinetIndex() + "번째 칸의 약을 먹지 않았어요. 찌르기로 피보호자에게 복약을 요청해보세요.");
                            try {
                                fcmService.sendPushAlarm(fcmRequestDto);
                            } catch (IOException e) {
                                throw new CustomException(ErrorCode.FCM_SERVER_ERROR);
                            }
                        }
                    });
        });
    }

    // ======================================================

    private LocalDate calculateNextPlannedDate(LocalDate today, Integer weekday) {
        DayOfWeek targetDayOfWeek = DayOfWeek.of(weekday);
        DayOfWeek todayDayOfWeek = today.getDayOfWeek();

        int daysToAdd = targetDayOfWeek.getValue() - todayDayOfWeek.getValue();
        if (daysToAdd < 0) {
            daysToAdd += 7;
        }

        return today.plusDays(daysToAdd);
    }

}


