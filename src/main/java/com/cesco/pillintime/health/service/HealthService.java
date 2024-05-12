package com.cesco.pillintime.health.service;

import com.cesco.pillintime.health.dto.HealthDto;
import com.cesco.pillintime.health.entity.Health;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.relation.entity.Relation;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.health.mapper.HealthMapper;
import com.cesco.pillintime.health.repository.HealthRepository;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.relation.repository.RelationRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Security;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final HealthRepository healthRepository;
    private final MemberRepository memberRepository;
    private final RelationRepository relationRepository;

    public void createHealth(@RequestBody HealthDto healthDto) {
        Integer steps = healthDto.getSteps();
        double cal = healthDto.getCal();
        LocalTime sleepTime = healthDto.getSleepTime();

        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Health health = new Health(steps, cal, sleepTime, member);
        healthRepository.save(health);
    }

    public List<HealthDto> getHealthByMemberId(Long targetId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = (targetId == null) ? requestMember :
                memberRepository.findById(targetId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<Health> healthList = healthRepository.findByMember(targetMember)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HEALTH));

        List<HealthDto> healthDtoList = new ArrayList<>();
        for (Health health : healthList) {
            HealthDto healthDto = HealthMapper.INSTANCE.toDto(health);
            healthDtoList.add(healthDto);
        }

        return healthDtoList;
    }
}
