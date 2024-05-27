package com.cesco.pillintime.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmRequestDto {

    private Long targetId;
    private String title;
    private String body;

}
