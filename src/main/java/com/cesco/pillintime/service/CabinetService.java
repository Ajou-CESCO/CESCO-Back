package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.CabinetDto;
import com.cesco.pillintime.dto.SensorDto;
import com.cesco.pillintime.entity.Cabinet;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.repository.CabinetRepository;
import com.cesco.pillintime.repository.MemberRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CabinetService {

    private final CabinetRepository cabinetRepository;
    private final MemberRepository memberRepository;

    public void createCabinet(CabinetDto cabinetDto) {
        Long id = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String uuid = cabinetDto.getUuid();

        Cabinet newCabinet = new Cabinet(uuid, member);
        cabinetRepository.save(newCabinet);
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
