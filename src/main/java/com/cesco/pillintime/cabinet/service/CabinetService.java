package com.cesco.pillintime.cabinet.service;

import com.cesco.pillintime.dto.SensorDto;
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

    public void createCabinet(String serialNumber, Long pk) {
        Long id = SecurityUtil.getCurrentMemberId();

        Member member;

        if(pk == null) {        // 본인 약통
             member = memberRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)); // 수정 바람
        } else {                // 타인 약통
            member = memberRepository.findById(pk)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER)); // 수정 바람
        }

        // 시리얼 번호로 약통 조회
        // PK(??) -> 유저 조회

        Cabinet cabinet = cabinetRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CABINET));

        cabinet.setOwnerId(member);
        cabinetRepository.save(cabinet);
    }

    public void getSensorData(SensorDto sensorDto) {

        String uuid = sensorDto.getUuid();
        int index = sensorDto.getIndex();

        Cabinet cabinet = cabinetRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CABINET));

        System.out.print(uuid);
        System.out.print(index);
    }
}
