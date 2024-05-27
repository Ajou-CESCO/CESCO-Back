package com.cesco.pillintime.api.adverse.dto;

import lombok.Data;

@Data
public class AdverseDto {

    private String serviceKey;
    private Integer pageNo;
    private Integer numOfRows;
    private String type;
    private String typeName;
    private String ingrCode;
    private String ingrName;
}