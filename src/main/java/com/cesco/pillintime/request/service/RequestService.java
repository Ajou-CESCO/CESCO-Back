package com.cesco.pillintime.request.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.request.dto.RequestDto;
import com.cesco.pillintime.request.entity.Request;
import com.cesco.pillintime.request.mapper.RequestMapper;
import com.cesco.pillintime.request.repository.RequestRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public Request createRequest(RequestDto requestDto) {
        Long id = SecurityUtil.getCurrentMemberId();

        Request request = RequestMapper.INSTANCE.toEntity(requestDto);
        request.setSenderId(id);

        return requestRepository.save(request);
    }

    public List<Request> getRelatedRequest() {
        String phone = SecurityUtil.getCurrentMemberPhone();
        return requestRepository.findByReceiverPhone(phone)
                .orElseThrow(()->new CustomException(ErrorCode.NOT_FOUND_REQUEST));
    }

    public void deleteRequestById(Long id) {
        requestRepository.deleteById(id);
    }
}