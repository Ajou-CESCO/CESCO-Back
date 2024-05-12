package com.cesco.pillintime.relation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RelationDto {

    private Long id;

    private String memberName;
    private String memberSsn;
    private String memberPhone;
    private Long cabinetId;

    @JsonProperty(value="isManager")
    private boolean isManager;

}
