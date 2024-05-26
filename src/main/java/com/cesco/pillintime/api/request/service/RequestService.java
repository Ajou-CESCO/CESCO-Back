package com.cesco.pillintime.api.request.service;

import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.request.dto.RequestDto;
import com.cesco.pillintime.api.request.entity.Request;
import com.cesco.pillintime.api.request.mapper.RequestMapper;
import com.cesco.pillintime.api.request.repository.RequestRepository;
import com.cesco.pillintime.fcm.dto.FcmMessageDto;
import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.service.FcmService;
import com.cesco.pillintime.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final MemberRepository memberRepository;
    private final FcmService fcmService;

    public Request createRequest(RequestDto requestDto) {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String receiverPhone = requestDto.getReceiverPhone();
        memberRepository.findByPhone(receiverPhone)
                .ifPresent((targetMember) -> {
                    FcmRequestDto fcmRequestDto = new FcmRequestDto(
                            targetMember.getId(),
                            "[약속시간] \uD83D\uDD14 띵동 \uD83D\uDD14",
                            member.getName() + " 님으로부터 보호관계 요청이 왔어요 \uD83D\uDC8C"
                    );

                    try {
                        fcmService.sendPushAlarm(fcmRequestDto);
                    } catch (IOException e) {
                        throw new CustomException(ErrorCode.FCM_SERVER_ERROR);
                    }
                });

        Request request = new Request(member, receiverPhone);
        return requestRepository.save(request);
    }


    public List<RequestDto> getRelatedRequest() {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String phone = member.getPhone();

        List<RequestDto> requestDtoList = new ArrayList<>();
        requestRepository.findByReceiverPhone(phone)
                .ifPresent(requests -> {
                    for (Request request : requests) {
                        RequestDto requestDto = RequestMapper.INSTANCE.toDto(request);
                        requestDtoList.add(requestDto);
                    }
                });

        return requestDtoList;
    }

    public void deleteRequestById(Long id) {
        requestRepository.deleteById(id);
    }
}