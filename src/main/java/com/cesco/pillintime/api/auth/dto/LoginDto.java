package com.cesco.pillintime.api.auth.dto;

import lombok.Data;

@Data
public class LoginDto {

    private String name;
    private String phone;
    private String ssn;

}
