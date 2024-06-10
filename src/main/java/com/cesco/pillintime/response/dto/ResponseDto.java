package com.cesco.pillintime.response.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class ResponseDto {

    private int status;
    private String message;
    private Object result;

    public ResponseDto() {
        this.status = 200;
        this.message = null;
        this.result = new HashMap<>();
    }
}
