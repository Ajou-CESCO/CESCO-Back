package com.cesco.pillintime.api.request.dto;

import lombok.Data;

@Data
public class RequestDto {

    private Long id;
    private Long senderId;
    private String senderName;
    private String senderPhone;
    private String receiverPhone;

}
