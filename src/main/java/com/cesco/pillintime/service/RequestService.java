package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.RequestDto;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.mapper.RequestMapper;
import com.cesco.pillintime.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    public Request createRequest(RequestDto requestDto) {
        Request request = RequestMapper.INSTANCE.toEntity(requestDto);
        return requestRepository.save(request);
    }

    public List<Request> getRelatedRequest() {
        return requestRepository.findBySenderId(1);
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }

}