package com.cesco.pillintime.request.service;

import com.cesco.pillintime.request.dto.RequestDto;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.request.entity.Request;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.request.mapper.RequestMapper;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.request.repository.RequestRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final MemberRepository memberRepository;

    public Request createRequest(RequestDto requestDto) {
        Long id = SecurityUtil.getCurrentMemberId();

        Request request = RequestMapper.INSTANCE.toEntity(requestDto);
        request.setSenderId(id);

        return requestRepository.save(request);
    }

    public List<Request> getRelatedRequest() {
        Long id = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException((ErrorCode.NOT_FOUND_USER)));

        if (member.getUserType() == 0) {    // manager
            return requestRepository.findBySenderId(id)
                    .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));
        } else {
            String phone = SecurityUtil.getCurrentMemberPhone();
            return requestRepository.findByReceiverPhone(phone)
                    .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_USER));
        }
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }

}