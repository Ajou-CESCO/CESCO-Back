package com.cesco.pillintime.dto;

import lombok.Data;

@Data
public class RelationDto {

    private Long id;

    private String managerName;
    private String managerUuid;

    private String clientName;
    private String clientUuid;
}
