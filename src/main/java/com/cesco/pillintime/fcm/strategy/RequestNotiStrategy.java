package com.cesco.pillintime.fcm.strategy;

import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.service.FcmService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component("requestStrategy")
public class RequestNotiStrategy implements FcmStrategy {

    private final FcmService fcmService;

    @Override
    public void execute(Map<String, Object> params) throws IOException {
        List<FcmRequestDto> fcmRequestDtoList = makeRequestDtoList(params);
        for (FcmRequestDto fcmRequestDto : fcmRequestDtoList) {
            fcmService.sendPushAlarm(fcmRequestDto, false);
        }
    }

    @Override
    public List<FcmRequestDto> makeRequestDtoList(Map<String, Object> params) {
        Member requestMember = (Member) params.get("requestMember");
        Member targetMember = (Member) params.get("targetMember");

        List<FcmRequestDto> fcmRequestDtoList = new ArrayList<>();
        FcmRequestDto fcmRequestDto = new FcmRequestDto();
        fcmRequestDto.setTargetId(targetMember.getId());
        fcmRequestDto.setTitle("[약속시간] \uD83D\uDD14 띵동 \uD83D\uDD14");
        fcmRequestDto.setBody(requestMember.getName() + " 님으로부터 보호관계 요청이 왔어요\uD83D\uDC8C");

        fcmRequestDtoList.add(fcmRequestDto);
        return fcmRequestDtoList;
    }
}
