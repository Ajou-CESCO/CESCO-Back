package com.cesco.pillintime.fcm.dto;

import lombok.Data;

@Data
public class FcmRequestDto {

    private Long targetId;
    private String title;
    private String body;

}
