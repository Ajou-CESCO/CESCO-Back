package com.cesco.pillintime.fcm.strategy;

import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.api.relation.repository.RelationRepository;
import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.service.FcmService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("managerOverLogStrategy")
@AllArgsConstructor
public class ManagerOverLogNotiStrategy implements FcmStrategy {

    private final RelationRepository relationRepository;
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
        Log log = (Log) params.get("log");
        Member client = log.getMember();

        List<FcmRequestDto> fcmRequestDtoList = new ArrayList<>();
        relationRepository.findByMember(client)
                .ifPresent((relationList) -> {
                    for (Relation relation : relationList) {
                        Member manager = relation.getManager();

                        FcmRequestDto fcmRequestDto = new FcmRequestDto();
                        fcmRequestDto.setTargetId(manager.getId());
                        fcmRequestDto.setTitle("[약속시간] \uD83D\uDEA8 알림 \uD83D\uDEA8");
                        fcmRequestDto.setBody(
                            "‼️ " + client.getName() + " 님이 " + log.getPlannedAt().toLocalTime() + "시에 " +
                                    log.getPlan().getCabinetIndex() + "번째 칸의 약을 먹지 않았어요. 찌르기로 피보호자에게 복약을 요청해보세요."
                        );

                        fcmRequestDtoList.add(fcmRequestDto);
                    }
                });

        return fcmRequestDtoList;
    }
}
