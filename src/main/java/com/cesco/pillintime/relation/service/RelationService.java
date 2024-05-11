package com.cesco.pillintime.relation.service;

import com.cesco.pillintime.relation.dto.RelationDto;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.relation.entity.Relation;
import com.cesco.pillintime.request.entity.Request;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.relation.repository.RelationRepository;
import com.cesco.pillintime.request.repository.RequestRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RequestRepository requestRepository;
    private final RelationRepository relationRepository;
    private final MemberRepository memberRepository;

    public void createRelation(Long requestId) {

        Member client = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REQUEST));

        // 요청을 전송한 사용자가 회원탈퇴 했을 경우
        Long managerId = request.getSenderId();
        Member manager = memberRepository.findById(managerId)
                .orElseThrow(() -> new CustomException((ErrorCode.NOT_FOUND_USER)));

        Relation relation = new Relation(manager, client);
        relationRepository.save(relation);
        requestRepository.delete(request);
    }

    public List<RelationDto> getRelationList() {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<Relation> relationList = relationRepository.findByMember(requestMember).orElse(null);
        if (relationList == null) {
            return null;
        }

        boolean isManager = requestMember.isManager();

        List<RelationDto> relationDtoList = new ArrayList<>();
        for (Relation relation : relationList) {
            RelationDto relationDto = new RelationDto();
            Member member = isManager ? relation.getClient() : relation.getManager();

            relationDto.setMemberName(member.getName());
            relationDto.setMemberPhone(member.getPhone());
            relationDto.setMemberSsn(member.getSsn());

            relationDtoList.add(relationDto);
        }

        return relationDtoList;
    }
}