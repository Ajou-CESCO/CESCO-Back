package com.cesco.pillintime.cabinet.service;

import com.cesco.pillintime.cabinet.dto.CabinetDto;
import com.cesco.pillintime.cabinet.dto.SensorDto;
import com.cesco.pillintime.cabinet.entity.Cabinet;
import com.cesco.pillintime.log.entity.Log;
import com.cesco.pillintime.log.entity.TakenStatus;
import com.cesco.pillintime.log.repository.LogRepository;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.cabinet.repository.CabinetRepository;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CabinetService {

    private final CabinetRepository cabinetRepository;
    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    public void createCabinet(CabinetDto cabinetDto) {
        String serial = cabinetDto.getSerial();
        Long ownerId = cabinetDto.getOwnerId();

        Member owner;

        if (ownerId == null) {
            owner = SecurityUtil.getCurrentMember()
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        } else {
            owner = memberRepository.findById(ownerId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        }

        Cabinet cabinet = cabinetRepository.findBySerial(serial)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CABINET));

        cabinet.setOwner(owner);
        owner.setCabinet(cabinet);
        cabinetRepository.save(cabinet);
        memberRepository.save(owner);
    }

    public void getSensorData(SensorDto sensorDto) {
        String serial = sensorDto.getSerial();
        int sensorIndex = sensorDto.getIndex();

        // Cabinet 및 Member 정보 가져오기
        Cabinet cabinet = cabinetRepository.findBySerial(serial)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CABINET));
        Member owner = memberRepository.findByCabinet(cabinet)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_OWNER));

        // 오늘의 로그 찾기
        LocalDate today = LocalDate.now();
        Log todayLog = logRepository.findByMemberAndPlannedAt(owner, today)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_LOG));

        // 로그 상태 업데이트
        todayLog.setTakenStatus(TakenStatus.COMPLETED);
        logRepository.save(todayLog);
    }
}
