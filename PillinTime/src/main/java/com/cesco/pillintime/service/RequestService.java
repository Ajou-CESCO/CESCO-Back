package com.cesco.pillintime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cesco.pillintime.repository.RequestRepository;
import com.cesco.pillintime.entity.Request;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public Request createRequest(Request request) {
        return requestRepository.save(request);
    }

    public List<Request> getAllRequest() {
        return requestRepository.findAll();
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }

}
