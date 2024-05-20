package com.cesco.pillintime.cabinet.service;

import com.cesco.pillintime.cabinet.dto.CabinetDto;
import com.cesco.pillintime.cabinet.dto.SensorDto;
import com.cesco.pillintime.cabinet.entity.Cabinet;
import com.cesco.pillintime.cabinet.repository.CabinetRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.log.entity.TakenStatus;
import com.cesco.pillintime.log.repository.LogRepository;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CabinetService {

    private final CabinetRepository cabinetRepository;
    private final MemberRepository memberRepository;
    private final LogRepository logRepository;
    private final SecurityUtil securityUtil;

    public void createCabinet(CabinetDto cabinetDto) {
        String serial = cabinetDto.getSerial();
        Long ownerId = cabinetDto.getOwnerId();

        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember;

        if (!requestMember.getId().equals(ownerId)) { // ??? -> requestMember.getId() == ownerId
             targetMember = memberRepository.findById(ownerId)
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        Cabinet cabinet = cabinetRepository.findBySerial(serial)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CABINET));

        if (cabinet.getOwner() != null) {
            throw new CustomException(ErrorCode.ALREADY_EXISTS_OWNER);
        } else {
            cabinet.setOwner(targetMember);
            targetMember.setCabinet(cabinet);
            cabinetRepository.save(cabinet);
            memberRepository.save(targetMember);
        }
    }

    public void deleteCabinet(Long cabinetId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Cabinet cabinet = cabinetRepository.findById(cabinetId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CABINET));

        Member targetMember = cabinet.getOwner();

        if (!requestMember.equals(targetMember)) {
            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        cabinet.setOwner(null);
        targetMember.setCabinet(null);
        cabinetRepository.save(cabinet);
        memberRepository.save(targetMember);
    }

    public void getSensorData(SensorDto sensorDto) {
        String serial = sensorDto.getSerial();
        int index = sensorDto.getIndex();

        System.out.println(serial);
        System.out.println(index);

        // Cabinet 정보 가져오기
        Cabinet cabinet = cabinetRepository.findBySerial(serial)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CABINET));

        Optional<Member> owner = memberRepository.findByCabinet(cabinet); // ??? -> Member owner = cabinet.getOwner();

        owner.ifPresent(member -> {
            // 현재 날짜, 시각 구하기
            LocalDate today = LocalDate.now();
            LocalTime currentTime = LocalTime.now();

            LocalTime rangeStartTime = currentTime.minusMinutes(30);
            LocalTime rangeEndTime = currentTime.plusMinutes(30);

            System.out.println(member.getName());
            System.out.println(member.getId());
            System.out.println(today);
            System.out.println(currentTime);
            System.out.println(rangeStartTime);
            System.out.println(rangeEndTime);

            // 타겟 로그 조회 후 존재할 시 업데이트
            logRepository.findTargetLog(member, today, index, rangeStartTime, rangeEndTime)
                    .ifPresent(log -> {
                        log.setTakenStatus(TakenStatus.COMPLETED);
                        logRepository.save(log);
                    });
        });
    }
}
