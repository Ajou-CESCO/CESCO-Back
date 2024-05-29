package com.cesco.pillintime.api.request.service;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.request.dto.RequestDto;
import com.cesco.pillintime.api.request.entity.Request;
import com.cesco.pillintime.api.request.mapper.RequestMapper;
import com.cesco.pillintime.api.request.repository.RequestRepository;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.fcm.strategy.FcmStrategy;

import com.cesco.pillintime.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final MemberRepository memberRepository;

    private final ApplicationContext context;

    @Transactional
    public Request createRequest(RequestDto requestDto) {
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String receiverPhone = requestDto.getReceiverPhone();

        Request request = requestRepository.findBySenderAndReceiverPhone(requestMember, receiverPhone)
                .orElseGet(() -> {
                    Request newRequest = new Request(requestMember, receiverPhone);
                    return requestRepository.save(newRequest);
                });

        System.out.println(request.getId());

//        memberRepository.findByPhone(receiverPhone)
//                .ifPresent((targetMember) -> {
//                    Map<String, Object> requestParams = new HashMap<>();
//                    requestParams.put("requestMember", requestMember);
//                    requestParams.put("targetMember", targetMember);
//
//                    FcmStrategy requestStrategy = context.getBean("requestStrategy", FcmStrategy.class);
//                    requestStrategy.execute(requestParams);
//                });
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

    @Transactional
    public void deleteRequestById(Long id) {
        requestRepository.deleteById(id);
    }
}