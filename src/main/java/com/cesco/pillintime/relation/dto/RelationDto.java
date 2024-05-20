package com.cesco.pillintime.relation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
