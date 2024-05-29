package com.cesco.pillintime.api.log.service;

import com.cesco.pillintime.api.log.dto.LogDto;
import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.log.entity.TakenStatus;
import com.cesco.pillintime.api.log.mapper.LogMapper;
import com.cesco.pillintime.api.log.repository.LogRepository;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.entity.Plan;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.fcm.strategy.FcmStrategy;
import com.cesco.pillintime.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final PlanRepository planRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;
    private final ApplicationContext context;

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

//    @Scheduled(cron = "0 1/31 * * * *")
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void updateDoseLogByCurrentTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime targetTime = currentTime.minusMinutes(30);

        LocalDateTime startOfSecond = currentTime.truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime endOfSecond = startOfSecond.plus(999, ChronoUnit.MILLIS);

        // 현재 시각과 일치하는 예정 계획이 있을 경우 푸시알림
        logRepository.findPlannedLog(startOfSecond, endOfSecond)
                .ifPresent((plannedLogList) -> {
                    for (Log log : plannedLogList) {
                        Map<String, Object> requestParams = new HashMap<>();
                        requestParams.put("log", log);

                        FcmStrategy clientPlanStrategy = context.getBean("clientPlanStrategy", FcmStrategy.class);
                        clientPlanStrategy.execute(requestParams);
                    }
                });

        // 예정 시각보다 30분 초과한 미완료된 로그들을 조회하여 업데이트 및 푸시알림
        List<Log> incompletedLogList = logRepository.findIncompleteLog(targetTime);
        incompletedLogList.forEach(log -> {
            log.setTakenStatus(TakenStatus.TIMED_OUT);
            logRepository.save(log);

            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("log", log);

            FcmStrategy clientLogStrategy = context.getBean("clientOverLogStrategy", FcmStrategy.class);
            FcmStrategy managerLogStrategy = context.getBean("managerOverLogStrategy", FcmStrategy.class);
            clientLogStrategy.execute(requestParams);
            managerLogStrategy.execute(requestParams);
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


