package com.cesco.pillintime.dto;

import lombok.Data;

@Data
public class RequestDto {

    private int id;
    private int senderId;
    private String receiverName;
    private String receiverNumber;
    private String receiverSsn;

}
