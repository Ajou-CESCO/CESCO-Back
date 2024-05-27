package com.cesco.pillintime.api.health.service;

import com.cesco.pillintime.api.health.repository.HealthRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.health.dto.HealthDto;
import com.cesco.pillintime.api.health.entity.Health;
import com.cesco.pillintime.api.health.mapper.HealthMapper;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final HealthRepository healthRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

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

    public List<HealthDto> getHealthByMemberId(Long targetId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = (targetId == null) ? requestMember :
                memberRepository.findById(targetId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Health maxHealth = healthRepository.findMaxLocalDateTimeByMember(targetMember).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HEALTH));

        List<HealthDto> healthDtoList = new ArrayList<>();
        HealthDto healthDto = HealthMapper.INSTANCE.toDto(maxHealth);
        healthDtoList.add(healthDto);

        return healthDtoList;
    }
}
