package com.cesco.pillintime.dto;

import lombok.Data;

@Data
public class MemberDto {

    private Long id;

    private String name;

    private String ssn;

    private String phone;

    private Integer gender;

    private String uuid;

    private Integer userType;
}