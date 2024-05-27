package com.cesco.pillintime.api.relation.service;

import com.cesco.pillintime.api.relation.dto.RelationDto;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.api.relation.repository.RelationRepository;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.request.entity.Request;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.request.repository.RequestRepository;
import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.service.FcmService;
import com.cesco.pillintime.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RequestRepository requestRepository;
    private final RelationRepository relationRepository;
    private final FcmService fcmService;

    public void createRelation(Long requestId) {
        Member client = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REQUEST));

        Member manager = request.getSender();

        Relation relation = new Relation(manager, client);
        relationRepository.save(relation);
        requestRepository.delete(request);

        FcmRequestDto fcmRequestDto = new FcmRequestDto(
                manager.getId(), "[약속시간] 📢 보호관계 수락 알림 📢",
                client.getName() + " 님이 보호관계를 수락했어요 \uD83D\uDE04 지금 바로 " + client.getName() + " 님을 케어해보세요."
        );
        try {
            fcmService.sendPushAlarm(fcmRequestDto);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FCM_SERVER_ERROR);
        }
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

    public void deleteRelation(Long relationId) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Relation relation = relationRepository.findById(relationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RELATION));

        if (requestMember.equals(relation.getClient()) || requestMember.equals(relation.getManager())) {
            relationRepository.delete(relation);
        } else {
            throw new CustomException(ErrorCode.INVALID_USER_ACCESS);
        }
    }
}