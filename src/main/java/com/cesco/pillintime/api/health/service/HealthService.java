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
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final HealthRepository healthRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createHealth(@RequestBody HealthDto healthDto) {
        Integer steps = healthDto.getSteps();
        double cal = healthDto.getCal();
        LocalTime sleepTime = healthDto.getSleepTime();

        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        System.out.println("member.getId() = " + member.getId());
        Health health = new Health(steps, cal, sleepTime, member);
        healthRepository.save(health);
    }

    public HealthDto getHealthByMemberId(Long targetId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = (targetId == null) ? requestMember :
                memberRepository.findById(targetId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Health maxHealth = healthRepository.findMaxLocalDateTimeByMember(targetMember).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HEALTH));

        HealthDto healthDto = HealthMapper.INSTANCE.toDto(maxHealth);
        Integer averStep = 6482;
        healthDto.setAverageSteps(averStep);

        String step = getStringStep(targetMember, healthDto, averStep);
        healthDto.setStepsMessage(step);

        healthDto.setSleepTimeMessage("어제보다 " + 99 + "시간 더 주무셨어요.");

        return healthDto;
    }

    @NotNull
    private static String getStringStep(Member targetMember, HealthDto healthDto, Integer averStep) {
        int currentAge = LocalDate.now().getYear()%100 - Integer.parseInt(targetMember.getSsn().substring(1, 2));

        String ageGroup = (currentAge < 0 ? currentAge+100 : currentAge) /10*10 + "대 ";
        String ageMessage = healthDto.getSteps() < averStep ?
                "평균까지 " + (averStep - healthDto.getSteps()) + "걸음 남았습니다." :
                "평균보다 " + (healthDto.getSteps()- averStep) + "걸음 더 걸었어요!";
        return ageGroup+ageMessage;
    }
}
