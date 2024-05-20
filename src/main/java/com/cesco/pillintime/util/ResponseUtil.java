package com.cesco.pillintime.util;

import com.cesco.pillintime.dto.ResponseDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
public class ResponseUtil {

    public static ResponseEntity<ResponseDto> makeResponse(
            int statusCode,
            String message,
            Object result
    ) {
        ResponseDto responseDto = new ResponseDto();
        HttpHeaders headers = new HttpHeaders();

        responseDto.setStatus(statusCode);
        responseDto.setMessage(message);

        if (result != null) {
            responseDto.setResult(result);
        }

        // 애러코드와 응답코드 조정
        int httpStatus = statusCode < 10000 ? statusCode : statusCode / 100;
        return new ResponseEntity<>(responseDto, headers, httpStatus);
    }

}
