package com.cesco.pillintime.entity;

import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor
public class User { // 유저 객체를 저장하기 위한 상요

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(nullable = false, length = 25)
    private String ssn;

    @Column(nullable = false, length = 14)
    private String phone;

    @Builder
    public User(Long id, String name, String ssn, String phone) {
        this.id = id;
        this.name = name;
        this.ssn = ssn;
        this.phone = phone;
    }
}