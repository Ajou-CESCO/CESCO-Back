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
    public void createHealth(@RequestBody HealthDto healthDto) {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Health health = HealthMapper.INSTANCE.toEntity(healthDto);
        health.setMember(member);
        healthRepository.save(health);
    }

    public HealthDto getHealthByMemberId(Long targetId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        System.out.println(requestMember.getName());

        Member targetMember = (targetId == null) ? requestMember :
                memberRepository.findById(targetId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Optional<Health> optionalHealth = healthRepository.findNewestHealth(targetMember);
        if (optionalHealth.isEmpty()) {
            return null;
        }

        HealthDto healthDto = HealthMapper.INSTANCE.toDto(optionalHealth.get());


        // 현재 나이, 나이 대 생성
        Integer currentAge = (LocalDate.now().getYear()%100 - Integer.parseInt(targetMember.getSsn().substring(1, 2)));
        long ageGroup = (currentAge < 0 ? currentAge+100 : currentAge) /10*10;


        // 평균 도보, 메시지 생성
//        Long step = healthDto.getSteps();
        Long averStep = (long) meanStep[(int) (ageGroup/10)];
        String stepMessage = ageGroup + "대 "+ getStringStep(healthDto.getSteps(), averStep);

        // 권장 소모 칼로리, 메시지 생성
        Long recommendCalorie = Long.valueOf(calorieMap.floorEntry(currentAge).getValue());
        String calorieMessage = recommendCalorie+"kcal";

        // 현재 나이 대 권장 심박수, 메시지 생성
        Long heartRate = Long.valueOf(heartRateMap.floorEntry(currentAge).getValue());
        String heartRateMessage = heartRate + "-" + (heartRate+10) + "bpm";


        // 권장 수면, 메시지 생성
        Long todaySleepTime = healthRepository.findByHealth(LocalDate.now()).orElse(null).getSleepTime();
        Long yesterdaySleepTime = healthRepository.findByHealth(LocalDate.now().minusDays(1)).orElse(null).getSleepTime();

        String sleepMessage = getStringSleep(todaySleepTime, yesterdaySleepTime);

        Long recommendSleepTime = sleepTimeMap.floorEntry(currentAge).getValue();


        healthDto.setAgeGroup(ageGroup);

//        healthDto.setSteps(step);
        healthDto.setAverStep(averStep);
        healthDto.setStepsMessage(stepMessage);

        healthDto.setCalorie(recommendCalorie);
        healthDto.setCalorieMessage(calorieMessage);

        healthDto.setHeartRate(heartRate);
        healthDto.setHeartRateMessage(heartRateMessage);

        healthDto.setSleepTime(todaySleepTime);
        healthDto.setRecommendSleepTime(recommendSleepTime);
        healthDto.setSleepTimeMessage(sleepMessage);

        return healthDto;
    }

    private static String getStringStep(Long Step, Long averStep) {
        return (Step < averStep ?
                "평균까지 " + (averStep - Step) + "보 남았습니다." :
                "평균보다 " + (Step - averStep) + "보 더 걸었어요!");
    }

    private static String getStringSleep(Long todaySleepTime, Long yesterdaySleepTime) {
        return (todaySleepTime < yesterdaySleepTime ?
                "어제보다 " + (yesterdaySleepTime-todaySleepTime) + "시간 더 주무셨어요." :
                "어제보다 " + (todaySleepTime-yesterdaySleepTime) + "시간 덜 주무셨어요.");
    }
}
