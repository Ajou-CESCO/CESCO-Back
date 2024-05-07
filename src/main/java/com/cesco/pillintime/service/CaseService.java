package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.entity.Case;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.repository.CaseRepository;
import com.cesco.pillintime.repository.MemberRepository;
import com.cesco.pillintime.util.JwtUtil;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public void createCase(String uuid){

        Long id = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Case newCase = new Case();
        newCase.setUuid(uuid);
        newCase.setOwnerId(member);

        caseRepository.save(newCase);
    }
}
