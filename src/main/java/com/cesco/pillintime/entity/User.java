package com.cesco.pillintime.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "member")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name="member_Id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column
    private String phone;

    @Column private String uuid;
    @Column(unique = true) private String ssn;
    @Column private int gender;
    @Column(name = "user_type") private Integer userType;
    @Column(name = "is_subscriber")  private boolean isSubscriber;
    @Column(name = "has_case")  private boolean hasCase;

    public User() {}

    public User(String name, String phone, String ssn, int gender, Integer userType){

        if(name == null || ssn == null || phone == null || name.isBlank() || ssn.isBlank() || phone.isBlank()){
            throw new IllegalArgumentException(String.format("정확히 입력해주십시오"));
        }

        this.name = name;
        this.ssn = ssn;
        this.phone = phone;
        this.gender = gender;
        this.userType = userType;
    }

}
