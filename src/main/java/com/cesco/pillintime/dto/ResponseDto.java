package com.cesco.pillintime.dto;

import lombok.Data;

@Data
public class ResponseDto {

    private int status;
    private String message;
    private Object result;

    public ResponseDto() {
        this.status = 200;
        this.message = null;
        this.result = null;
    }
}
