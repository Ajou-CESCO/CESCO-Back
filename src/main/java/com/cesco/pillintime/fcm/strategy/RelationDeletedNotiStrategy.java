package com.cesco.pillintime.fcm.strategy;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.service.FcmService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component("relationDeletedStrategy")
public class RelationDeletedNotiStrategy implements FcmStrategy {

    private final FcmService fcmService;

    @Override
    public void execute(Map<String, Object> params) {
        List<FcmRequestDto> fcmRequestDtoList = makeRequestDtoList(params);
        for (FcmRequestDto fcmRequestDto : fcmRequestDtoList) {
            fcmService.sendPushAlarm(fcmRequestDto, false);
        }
    }

    @Override
    public List<FcmRequestDto> makeRequestDtoList(Map<String, Object> params) {
        Member requestMember = (Member) params.get("requestMember");
        Relation relation = (Relation) params.get("relation");

        Member targetMember = null;
        if (relation != null) {
            if (relation.getManager().equals(requestMember)) {
                targetMember = relation.getClient();
            } else if (relation.getClient().equals(requestMember)) {
                targetMember = relation.getManager();
            }
        }

        List<FcmRequestDto> fcmRequestDtoList = new ArrayList<>();
        FcmRequestDto fcmRequestDto = new FcmRequestDto();
        fcmRequestDto.setTargetId(targetMember.getId());
        fcmRequestDto.setTitle("[약속시간] \uD83D\uDEA8 알림 \uD83D\uDEA8");
        fcmRequestDto.setBody("\uD83D\uDE2D " + requestMember.getName() + " 님이 나와의 보호 관계를 종료했어요.");

        fcmRequestDtoList.add(fcmRequestDto);
        return fcmRequestDtoList;
    }
}