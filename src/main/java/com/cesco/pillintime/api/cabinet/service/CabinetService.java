package com.cesco.pillintime.api.cabinet.service;

import com.cesco.pillintime.api.cabinet.dto.CabinetDto;
import com.cesco.pillintime.api.cabinet.entity.Cabinet;
import com.cesco.pillintime.api.cabinet.repository.CabinetRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CabinetService {

    private final CabinetRepository cabinetRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    @Transactional
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

    @Transactional
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
}
