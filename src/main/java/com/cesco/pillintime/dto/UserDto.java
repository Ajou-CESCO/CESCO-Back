package com.cesco.pillintime.dto;

import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String name;

    private String ssn;

    private String phone;

    private int gender;

    private String uuid;

    private Integer userType;
}