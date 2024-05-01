package com.cesco.pillintime.dto;

import lombok.Data;

@Data
public class MemberDto {

    private Long id;
    private String uuid;
    private String ssn;
    private String name;
    private String phone;
    private Integer gender;
    private Integer userType;
}