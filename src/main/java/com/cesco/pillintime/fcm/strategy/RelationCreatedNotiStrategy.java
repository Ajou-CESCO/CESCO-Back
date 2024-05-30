package com.cesco.pillintime.fcm.strategy;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.service.FcmService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component("relationCreatedStrategy")
public class RelationCreatedNotiStrategy implements FcmStrategy {

    private final FcmService fcmService;

    @Override
    public void execute(Map<String, Object> params) {
        List<FcmRequestDto> fcmRequestDtoList = makeRequestDtoList(params);
        for (FcmRequestDto fcmRequestDto : fcmRequestDtoList) {
            fcmService.sendPushAlarm(fcmRequestDto, true);
        }
    }

    @Override
    public List<FcmRequestDto> makeRequestDtoList(Map<String, Object> params) {
        Member requestMember = (Member) params.get("requestMember");
        Member targetMember = (Member) params.get("targetMember");
        String requestMemberName = requestMember.getName();

        List<FcmRequestDto> fcmRequestDtoList = new ArrayList<>();
        FcmRequestDto fcmRequestDto = new FcmRequestDto();
        fcmRequestDto.setTargetId(targetMember.getId());
        fcmRequestDto.setTitle("[약속시간] 📢 보호관계 수락 알림 📢");
        fcmRequestDto.setBody(
                requestMemberName + " 님이 보호관계를 수락했어요 \uD83D\uDE04 지금 바로 " + requestMemberName + " 님을 케어해보세요."
        );

        fcmRequestDtoList.add(fcmRequestDto);
        return fcmRequestDtoList;
    }
}
