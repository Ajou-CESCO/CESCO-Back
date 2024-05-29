package com.cesco.pillintime.fcm.strategy;

import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.service.FcmService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("clientOverLogStrategy")
@AllArgsConstructor
public class ClientOverLogNotiStrategy implements FcmStrategy {

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
        Log log = (Log) params.get("log");

        List<FcmRequestDto> fcmRequestDtoList = new ArrayList<>();
        FcmRequestDto fcmRequestDto = new FcmRequestDto();
        fcmRequestDto.setTargetId(log.getMember().getId());
        fcmRequestDto.setTitle("[약속시간] \uD83D\uDEA8 알림 \uD83D\uDEA8");
        fcmRequestDto.setBody("‼️ " + log.getPlannedAt().toLocalTime() + " 에 " + log.getPlan().getCabinetIndex() + "번째 칸의 약을 먹지 않았어요.");

        fcmRequestDtoList.add(fcmRequestDto);
        return fcmRequestDtoList;
    }
}