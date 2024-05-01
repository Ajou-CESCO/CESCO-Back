package com.cesco.pillintime.dto;

import lombok.Data;

@Data
public class RequestDto {

    private Long senderId;
    private String receiverName;
    private String receiverNumber;
    private String receiverSsn;

}
