package com.cesco.pillintime.api.relation.dto;

import lombok.Data;

@Data
public class RelationDto {

    private Long id;
    private Long memberId;
    private String memberName;
    private String memberSsn;
    private String memberPhone;
    private Long cabinetId;

}
