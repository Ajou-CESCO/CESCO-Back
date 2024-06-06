package com.cesco.pillintime.api.init.service;

import com.cesco.pillintime.api.init.dto.ExtendedRelationDto;
import com.cesco.pillintime.api.init.dto.InitDto;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.plan.repository.PlanRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.member.dto.MemberDto;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.mapper.MemberMapper;
import com.cesco.pillintime.api.relation.dto.RelationDto;
import com.cesco.pillintime.api.relation.service.RelationService;
import com.cesco.pillintime.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitService {

    private final RelationService relationService;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;

    public InitDto getInitialInfo() {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        MemberDto memberDto = MemberMapper.INSTANCE.toDto(member);

        List<RelationDto> relationDtoList = relationService.getRelationList();
        List<ExtendedRelationDto> extendedRelationDtoList = new ArrayList<>();
        for (RelationDto relationDto : relationDtoList) {
            Long memberId = relationDto.getMemberId();
            memberRepository.findById(memberId)
                    .ifPresent((targetMember) -> {
                        List<Long> cabinetIndexList = planRepository.findUsingCabinetIndex(targetMember);
                        ExtendedRelationDto exRelationDto = new ExtendedRelationDto(relationDto, cabinetIndexList);

                        extendedRelationDtoList.add(exRelationDto);
                    });

        }
        return new InitDto(memberDto, extendedRelationDtoList);
    }
}
