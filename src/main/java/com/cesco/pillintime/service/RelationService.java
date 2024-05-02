package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.RelationDto;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.entity.Relation;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.mapper.RelationMapper;
import com.cesco.pillintime.repository.MemberRepository;
import com.cesco.pillintime.repository.RelationRepository;
import com.cesco.pillintime.repository.RequestRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


        Relation relation = new Relation(managerId, clientId);
        relationRepository.save(relation);
        requestRepository.delete(request);

    }

    public List<Relation> getRelationList() {

        Long id = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException((ErrorCode.NOT_FOUND_USER)));

        return relationRepository.findByMemberId(id);
    }

}