package com.cesco.pillintime.fcm.strategy;

import com.cesco.pillintime.fcm.dto.FcmRequestDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FcmStrategy {

    void execute(Map<String, Object> params) throws IOException;
    List<FcmRequestDto> makeRequestDtoList(Map<String, Object> params);

}
