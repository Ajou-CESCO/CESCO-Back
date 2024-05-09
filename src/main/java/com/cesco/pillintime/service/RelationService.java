package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.RelationDto;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.entity.Relation;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.repository.MemberRepository;
import com.cesco.pillintime.repository.RelationRepository;
import com.cesco.pillintime.repository.RequestRepository;
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

        Long clientId = SecurityUtil.getCurrentMemberId();
        Member client = memberRepository.findById(clientId)
                .orElseThrow(() -> new CustomException((ErrorCode.NOT_FOUND_USER)));

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
        Long id = SecurityUtil.getCurrentMemberId();

        Member requester = memberRepository.findById(id)
                .orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_USER));

        List<Relation> relations = relationRepository.findByMemberId(requester.getId());
        if (relations == null) {
            return null;
        }

        List<RelationDto> relationDtoList = new ArrayList<>();

        for (Relation relation : relations) {
            RelationDto relationDto = new RelationDto();

            Member member = requester.getUserType() == 0 ? relation.getClient() : relation.getManager();
            if (member != null) {
                if(member.getUserType() == 0) { // 보호자
                    relationDto.setClientName(member.getName());
                    relationDto.setClientUuid(member.getUuid());
                }

                else if(member.getUserType() == 1) { // 피보호자
                    relationDto.setManagerName(member.getName());
                    relationDto.setManagerUuid(member.getUuid());
                }
            }

            relationDtoList.add(relationDto);
        }

        return relationDtoList;
    }
}