package com.cesco.pillintime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MemberDto {

    private Long id;
    private String uuid;
    private String name;
    private String ssn;
    private String phone;
    private Integer gender;
    private Integer userType;

    @JsonProperty(value = "isManager")
    private boolean isManager;

    @JsonProperty(value = "isSubscriber")
    private boolean isSubscriber;

    @JsonProperty(value = "hasCase")
    private boolean hasCase;

}