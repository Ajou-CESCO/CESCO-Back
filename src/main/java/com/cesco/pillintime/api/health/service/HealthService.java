package com.cesco.pillintime.api.health.service;

import com.cesco.pillintime.api.health.dto.HealthDto;
import com.cesco.pillintime.api.health.entity.Health;
import com.cesco.pillintime.api.health.mapper.HealthMapper;
import com.cesco.pillintime.api.health.repository.HealthRepository;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final HealthRepository healthRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    int[] meanStep = { 0, 6704, 7522, 8583, 10346, 11191, 11763, 9966};

    private static final NavigableMap<Integer, Long> sleepTimeMap = new TreeMap<>();

    static {
        sleepTimeMap.put(0, 12L);  // 0-0
        sleepTimeMap.put(1, 11L);  // 1-2
        sleepTimeMap.put(3, 10L);  // 3-5
        sleepTimeMap.put(6, 9L);   // 6-13
        sleepTimeMap.put(14, 8L);  // 14-17
        sleepTimeMap.put(18, 7L);  // 18+
    }

    private static final NavigableMap<Integer, Integer> heartRateMap = new TreeMap<>();

    static {
        heartRateMap.put(0, 80); // 0대
        heartRateMap.put(10, 80); // 10대
        heartRateMap.put(20, 70); // 20-30대
        heartRateMap.put(40, 60); // 60세 이하
        heartRateMap.put(89, 50); // 이후 나이
    }

    private static final NavigableMap<Integer, Integer> calorieMap = new TreeMap<>();

    static {
        calorieMap.put(0, 2200);
        calorieMap.put(10, 2600);
        calorieMap.put(20, 2550);
        calorieMap.put(30, 2500);
        calorieMap.put(40, 2400);
        calorieMap.put(50, 2300);
        calorieMap.put(60, 2100);
        calorieMap.put(70, 1850);
    }
    @Transactional
    public void createHealth(@RequestBody HealthDto healthDto) {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // 요청한 사용자가 보호자일 경우 무시
        if (member.isManager()) {
            return;
        }

        Health health = HealthMapper.INSTANCE.toEntity(healthDto);
        health.setMember(member);
        healthRepository.save(health);
    }

    public HealthDto getHealthByMemberId(Long targetId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = (targetId == null) ? requestMember :
                memberRepository.findById(targetId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        }

        LocalDate today = LocalDateTime.now().toLocalDate();

        Optional<Health> todayHealthOptional = healthRepository.findRecentHealthByDate(targetMember, today);

        if (todayHealthOptional.isEmpty()) {
            return null;
        }

        HealthDto todayHealthDto = HealthMapper.INSTANCE.toDto(todayHealthOptional.get());

        // 현재 나이, 나이 대 생성
        int currentAge = (LocalDate.now().getYear() % 100 - Integer.parseInt(targetMember.getSsn().substring(0, 2)));
        int ageGroup = (currentAge < 0 ? currentAge + 100 : currentAge) / 10 * 10;
        // 평균 도보, 메시지 생성
        Long step = (todayHealthDto.getSteps() != null) ? todayHealthDto.getSteps() : 0L;
        Long averStep = (long) meanStep[ageGroup/10];
        String stepMessage = getStringStep(ageGroup, step, averStep);

        // 권장 소모 칼로리, 메시지 생성
        Long caloire = (todayHealthDto.getCalorie() != null) ? todayHealthDto.getCalorie() : 0L;
        Long recommendCalorie = Long.valueOf(calorieMap.floorEntry(ageGroup).getValue());
        String calorieMessage = recommendCalorie+"kcal";

        // 현재 나이 대 권장 심박수, 메시지 생성
        Long heartRate = (todayHealthDto.getHeartRate() != null) ? todayHealthDto.getHeartRate() : 0L;
        Long recommendHeartRate = Long.valueOf(heartRateMap.floorEntry(ageGroup).getValue());
        String heartRateMessage = recommendHeartRate + "-" + (recommendHeartRate + 10) + "bpm";

        // 권장 수면, 메시지 생성
        Long todaySleepTime = (todayHealthDto.getSleepTime() != null) ? todayHealthDto.getSleepTime() : 0L;

        Long recommendSleepTime = sleepTimeMap.floorEntry(ageGroup).getValue();
        String sleepMessage = getStringSleep(todaySleepTime, recommendSleepTime);

        todayHealthDto.setAgeGroup((long) ageGroup);
        todayHealthDto.setSteps(step);
        todayHealthDto.setAverStep(averStep);
        todayHealthDto.setStepsMessage(stepMessage);

        todayHealthDto.setCalorie(caloire);
        todayHealthDto.setCalorieMessage(calorieMessage);

        todayHealthDto.setHeartRate(heartRate);
        todayHealthDto.setHeartRateMessage(heartRateMessage);

        todayHealthDto.setSleepTime(todaySleepTime);
        todayHealthDto.setRecommendSleepTime(recommendSleepTime);
        todayHealthDto.setSleepTimeMessage(sleepMessage);

        return todayHealthDto;
    }

    private static String getStringStep(int ageGroup, Long step, Long averStep) {
        if (step == 0) {
            return "오늘 걸음수가 기록되지 않았어요";
        }

        return (step < averStep ?
                ageGroup + "대 권장까지 " + (averStep - step) + "보 남았어요" :
                ageGroup + "대 권장보다 " + (step - averStep) + "보 더 걸었어요!");
    }

    private static String getStringSleep(Long todaySleepTime, Long recommendSleepTime) {
        if (todaySleepTime == 0) {
            return "오늘 수면시간이 기록되지 않았어요";
        }

        if(todaySleepTime.equals(recommendSleepTime))
            return "권장 수면 시간과 동일하게 주무셨네요.";
        return (todaySleepTime > recommendSleepTime ?
                "권장보다 " + (todaySleepTime - recommendSleepTime) + "시간 더 주무셨어요." :
                "권장보다 " + (recommendSleepTime - todaySleepTime) + "시간 덜 주무셨어요.");
    }
}
