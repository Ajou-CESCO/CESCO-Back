package com.cesco.pillintime.fcm.dto;

import lombok.Data;

@Data
public class FcmRequestDto {

    private Long targetId;
    private String title;
    private String body;

    public FcmRequestDto(Long targetId, String title, String body) {
        this.targetId = targetId;
        this.title = title;
        this.body = body;
    }

}
