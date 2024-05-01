package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.RequestDto;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.mapper.RequestMapper;
import com.cesco.pillintime.repository.RequestRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Long id = SecurityUtil.getCurrentMemberId();
        return requestRepository.findBySenderId(id);
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }

}