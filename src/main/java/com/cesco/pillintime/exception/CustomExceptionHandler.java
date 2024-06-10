package com.cesco.pillintime.exception;

import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.response.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ResponseDto> handleCustomException(CustomException e) {
        int statusCode = e.errorCode.getCode();
        String message = e.errorCode.getMessage();

        return ResponseUtil.makeResponse(statusCode, message, null);
    }

}
