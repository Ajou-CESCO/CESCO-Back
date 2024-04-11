package com.cesco.pillintime.domain;

import lombok.Data;

@Data
public class User {
    int id;
    String uuid;
    String name;
    String phone_numebr;
    String social_security_number;
    int gender;
    boolean is_manager;
    boolean is_subsciber;
    boolean has_case;
}
