package com.cesco.pillintime.init.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.init.dto.InitDto;
import com.cesco.pillintime.member.dto.MemberDto;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.mapper.MemberMapper;
import com.cesco.pillintime.relation.dto.RelationDto;
import com.cesco.pillintime.relation.service.RelationService;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitService {

    private final RelationService relationService;

    public InitDto getInitialInfo() {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        MemberDto memberDto = MemberMapper.INSTANCE.toDto(member);

        List<RelationDto> relationDtoList = relationService.getRelationList();

        return new InitDto(memberDto, relationDtoList);
    }
}
