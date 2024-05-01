package com.cesco.pillintime.util;

import com.cesco.pillintime.dto.ResponseDto;
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
        responseDto.setResult(result);

        return new ResponseEntity<> (responseDto, headers, statusCode);
    }

}
