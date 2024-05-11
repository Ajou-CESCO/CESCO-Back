package com.cesco.pillintime.relation.dto;

import lombok.Data;

@Data
public class RelationDto {

    private Long id;

    private String memberName;
    private String memberSsn;
    private String memberPhone;
    private Long caseId;
    private boolean isManager;

}
