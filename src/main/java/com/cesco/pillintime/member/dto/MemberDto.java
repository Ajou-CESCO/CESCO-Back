package com.cesco.pillintime.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MemberDto {

    private Long id;
    private String name;
    private String ssn;
    private String phone;
    private Integer gender;

    @JsonProperty(value = "isManager")
    private boolean isManager;

    @JsonProperty(value = "isSubscriber")
    private boolean isSubscriber;

    @JsonProperty(value = "hasCase")
    private boolean hasCase;
    public MemberDto(){}

    public MemberDto(Long id, String name, String ssn, String phone, Integer gender, boolean isManager, boolean isSubscriber, boolean hasCase) {
        this.id = id;
        this.name = name;
        this.ssn = ssn;
        this.phone = phone;
        this.gender = gender;
        this.isManager = isManager;
        this.isSubscriber = isSubscriber;
        this.hasCase = hasCase;
    }
}