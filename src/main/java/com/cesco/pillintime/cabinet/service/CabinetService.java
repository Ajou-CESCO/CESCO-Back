package com.cesco.pillintime.cabinet.service;

import com.cesco.pillintime.cabinet.dto.CabinetDto;
import com.cesco.pillintime.cabinet.dto.SensorDto;
import com.cesco.pillintime.cabinet.entity.Cabinet;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.cabinet.repository.CabinetRepository;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CabinetService {

    private final CabinetRepository cabinetRepository;
    private final MemberRepository memberRepository;

    public void createCabinet(CabinetDto cabinetDto) {
        String serial = cabinetDto.getSerial();
        Long ownerId = cabinetDto.getOwnerId();

        Member owner = null;

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
        cabinetRepository.save(cabinet);
    }

    public void getSensorData(SensorDto sensorDto) {
        String serial = sensorDto.getSerial();
        int index = sensorDto.getIndex();

        Cabinet cabinet = cabinetRepository.findBySerial(serial)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CABINET));
    }
}
