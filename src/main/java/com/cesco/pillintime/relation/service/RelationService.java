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

    public void createRelation(Long requestId) {

        Member client = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REQUEST));

        Member manager = request.getSender();

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

            relationDto.setId(relation.getId());
            relationDto.setMemberId(member.getId());
            relationDto.setMemberName(member.getName());
            relationDto.setMemberPhone(member.getPhone());
            relationDto.setMemberSsn(member.getSsn());
            relationDto.setCabinetId(member.getCabinet() != null ? member.getCabinet().getId() : 0);

            relationDtoList.add(relationDto);
        }

        return relationDtoList;
    }
}