package com.cesco.pillintime.api.request.service;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.request.dto.RequestDto;
import com.cesco.pillintime.api.request.entity.Request;
import com.cesco.pillintime.api.request.mapper.RequestMapper;
import com.cesco.pillintime.api.request.repository.RequestRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final MemberRepository memberRepository;

    public Request createRequest(RequestDto requestDto) {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String receiverPhone = requestDto.getReceiverPhone();

        Request request = requestRepository.findBySenderAndReceiverPhone(member, receiverPhone)
                .orElseGet(() -> new Request(member, receiverPhone));

        return request;
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