package com.cesco.pillintime.request.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.request.dto.RequestDto;
import com.cesco.pillintime.request.entity.Request;
import com.cesco.pillintime.request.mapper.RequestMapper;
import com.cesco.pillintime.request.repository.RequestRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public Request createRequest(RequestDto requestDto) {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String receiverPhone = requestDto.getReceiverPhone();

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