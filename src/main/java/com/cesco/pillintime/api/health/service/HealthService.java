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
        heartRateMap.put(19, 80); // 10대
        heartRateMap.put(39, 70); // 20-30대
        heartRateMap.put(59, 60); // 60세 이하
        heartRateMap.put(89, 50); // 이후 나이
    }

    private static final NavigableMap<Integer, Integer> calorieMap = new TreeMap<>();

    static {
        calorieMap.put(19, 2600);
        calorieMap.put(29, 2550);
        calorieMap.put(39, 2500);
        calorieMap.put(49, 2400);
        calorieMap.put(59, 2300);
        calorieMap.put(64, 2100);
        calorieMap.put(69, 2100);
        calorieMap.put(74, 1850);
    }
    @Transactional
    public String createHealth(@RequestBody HealthDto healthDto) {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Long steps = healthDto.getSteps();
        Long cal = healthDto.getCalorie();
        Long heartRate = healthDto.getHeartRate();
        Long sleepTime = healthDto.getSleepTime();
        System.out.println("member.getId() = " + member.getId());
        Health health = new Health(steps, cal, heartRate, sleepTime, member);
//        Health health = HealthMapper.INSTANCE.toEntity(healthDto);
        health.setMember(member);
        healthRepository.save(health);

        return "성공";
    }

    public HealthDto getHealthByMemberId(Long targetId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = (targetId == null) ? requestMember :
                memberRepository.findById(targetId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        LocalDate today = LocalDateTime.now().toLocalDate();
        LocalDate yesterday = LocalDateTime.now().minusDays(1).toLocalDate();

        Optional<Health> todayHealthOptional = healthRepository.findRecentHealthByDate(targetMember, today);
        Optional<Health> yesterdayHealthOptional = healthRepository.findRecentHealthByDate(targetMember, yesterday);

        if (yesterdayHealthOptional.isEmpty()) {
            return null;
        }

        HealthDto todayHealthDto = null;
        if (todayHealthOptional.isPresent()) {
            todayHealthDto = HealthMapper.INSTANCE.toDto(todayHealthOptional.get());
        }
        HealthDto yesterdayHealthDto = HealthMapper.INSTANCE.toDto(yesterdayHealthOptional.get());

        // 현재 나이, 나이 대 생성
        Integer currentAge = (LocalDate.now().getYear() % 100 - Integer.parseInt(targetMember.getSsn().substring(1, 2)));
        long ageGroup = (currentAge < 0 ? currentAge + 100 : currentAge) / 10 * 10;

        // 평균 도보, 메시지 생성
        Long step = (todayHealthDto != null) ? todayHealthDto.getSteps() : 0L;
        Long averStep = (long) meanStep[(int) (ageGroup/10)];
        String stepMessage = getStringStep(ageGroup, step, averStep);

        // 권장 소모 칼로리, 메시지 생성
        Long caloire = (todayHealthDto != null) ? todayHealthDto.getCalorie() : 0L;
        Long recommendCalorie = Long.valueOf(calorieMap.floorEntry(currentAge).getValue());
        String calorieMessage = recommendCalorie+"kcal";

        // 현재 나이 대 권장 심박수, 메시지 생성
        Long heartRate = (todayHealthDto != null) ? todayHealthDto.getHeartRate() : 0L;
        Long recommendHeartRate = Long.valueOf(heartRateMap.floorEntry(currentAge).getValue());
        String heartRateMessage = recommendHeartRate + "-" + (recommendHeartRate + 10) + "bpm";

        // 권장 수면, 메시지 생성
        String sleepMessage;
        Long todaySleepTime = (todayHealthDto != null) ? todayHealthDto.getSleepTime() : 0L;
        if (yesterdayHealthDto != null) {
            Long yesterdaySleepTime = yesterdayHealthDto.getSleepTime();
            sleepMessage = getStringSleep(todaySleepTime, yesterdaySleepTime);
        } else {
            sleepMessage = "오늘 " + todaySleepTime + "시간 잤어요";
        }

        Long recommendSleepTime = sleepTimeMap.floorEntry(currentAge).getValue();

        if(todayHealthDto == null) todayHealthDto = new HealthDto();
        todayHealthDto.setAgeGroup(ageGroup);
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

    private static String getStringStep(Long ageGroup, Long step, Long averStep) {
        if (step == 0) {
            return "오늘 걸음수가 기록되지 않았어요";
        }

        return (step < averStep ?
                ageGroup + "대 평균까지 " + (averStep - step) + "보 남았습니다." :
                ageGroup + "대 평균보다 " + (step - averStep) + "보 더 걸었어요!");
    }

    private static String getStringSleep(Long todaySleepTime, Long yesterdaySleepTime) {
        if (todaySleepTime == 0) {
            return "오늘 수면시간이 기록되지 않았어요";
        }

        return (todaySleepTime > yesterdaySleepTime ?
                "어제보다 " + (todaySleepTime - yesterdaySleepTime) + "시간 더 주무셨어요." :
                "어제보다 " + (yesterdaySleepTime - todaySleepTime) + "시간 덜 주무셨어요.");
    }
}
