package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.HealthDto;
import com.cesco.pillintime.entity.Health;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.entity.Relation;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.mapper.HealthMapper;
import com.cesco.pillintime.repository.HealthRepository;
import com.cesco.pillintime.repository.MemberRepository;
import com.cesco.pillintime.repository.RelationRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Time;
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
        Time sleepTime = healthDto.getSleepTime();
        int weekday = healthDto.getDate(); // 프론트에서 보내줄 수 있나요?

        Long id = SecurityUtil.getCurrentMemberId();

        Member requester = memberRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));

        List<Health> requesterHealth = healthRepository.findByOwnerId(requester)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HEALTH));

        Health health;
        // 건강 데이터 암호화해야 하는가?

        if (requesterHealth == null) {
            health = new Health(steps, cal, sleepTime, weekday, requester);
        }
        else {
            health = healthRepository.findByDate(weekday)
                    .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_HEALTH));
            health.setSteps(steps);
            health.setCal(cal);
            health.setSleepTime(sleepTime);
            health.setWeekday(weekday);
        }

        healthRepository.save(health);
    }

    public HealthDto getHealthById(String uuid) {
        /*
        1. UUID 유저 확인 (탈퇴 여부, 본인 여부)
        2. (토큰 & uuid) 관계 확인
        3. 건강 데이터 반환
        */
        Long id = SecurityUtil.getCurrentMemberId();

        Member requester = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (uuid.isEmpty()){ // 본인 정보 확인
            List<Health> healthList = healthRepository.findByOwnerId(requester)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HEALTH));
            int max = -1;

            Health healthMax = null;
            for (Health health : healthList) {
                if (health.getWeekday() > max) {
                    max = health.getWeekday();
                    healthMax = health;
                }
            }

            return HealthMapper.INSTANCE.toDto(healthMax);
        }
        else {              // 타인 정보 확인
            Member target = memberRepository.findByUuid(uuid);
            // ManyToOne 추가함 -> 리포 타입 변경해야 됨
            List<Relation> relationList = relationRepository.findByMember(requester)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RELATION));
            Health healthMax = null;

            for (Relation relation : relationList) {
                String memberUuid = relation.getClient().getUuid();
                if (memberUuid.equals(uuid)) {

                    List<Health> healthList = healthRepository.findByOwnerId(target)
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HEALTH));
                    int max = -1;

                    for (Health health : healthList) {
                        if (health.getWeekday() > max) {
                            max = health.getWeekday();
                            healthMax = health;
                        }
                    }
                }
            }
            return HealthMapper.INSTANCE.toDto(healthMax);
        }
    }

    // 정보 조회는 일주일치 건강 데이터를 보여주는 것인가? 아니면 월요일부터 정보를 보여주는 것인가?
    public List<HealthDto> getHealth(String uuid){
        Long id = SecurityUtil.getCurrentMemberId();

        Member requester = memberRepository.findById(id)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));

        if (uuid.isEmpty()) { // 본인 정보 확인
            List<Health> healthList = healthRepository.findByOwnerId(requester)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HEALTH));

            List<HealthDto> healthDtos = null;
            for (Health health : healthList) {
                HealthDto healthDto = HealthMapper.INSTANCE.toDto(health);
                healthDtos.add(healthDto);
            }
            return healthDtos;
        }
        else {              // 타인 정보 확인
            Member target = memberRepository.findByUuid(uuid);

            // ManyToOne 추가함 -> 리포 타입 변경해야 됨
            List<Relation> relationList = relationRepository.findByMember(requester)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RELATION));

            for (Relation relation : relationList) {
                String memberUuid = relation.getClient().getUuid();
                if (memberUuid.equals(uuid)) {
                    List<Health> healthList = healthRepository.findByOwnerId(target)
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_HEALTH));

                    List<HealthDto> healthDtos = null;
                    for (Health health : healthList) {
                        HealthDto healthDto = HealthMapper.INSTANCE.toDto(health);
                        healthDtos.add(healthDto);
                    }
                    return healthDtos;
                }
            }
        }
        return null;
    }
}
