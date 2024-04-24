package com.cesco.pillintime.dto;

import lombok.Data;

@Data
public class MessageDto {

    private int status;
    private String message;
    private Object data;

    public MessageDto() {
        this.status = 200;
        this.message = null;
        this.data = null;
    }
}
