package com.cesco.pillintime.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class Member {

    @Id
    @Column
    @JoinColumn(name="memberId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(unique = true)
    private String ssn;

    @Column(unique = true)
    private String phone;

    @Column
    private Integer gender;

    @Column
    private Integer userType;

    @Column
    private boolean isManager = false;

    @Column
    private boolean isSubscriber = false;

    @Column
    private boolean hasCase = false;

    public void setSsn(String ssn) {
        this.ssn = ssn;        // 성별 확인
        int gender = Character.getNumericValue(ssn.charAt(7));
        if (gender % 2 == 0){
            gender = 0; // female
        } else {
            gender = 1; // male
        }
        this.gender = gender;
    }

    public Member(String name, String phone, String ssn, Integer userType){

        if(name == null || ssn == null || phone == null || name.isBlank() || ssn.isBlank() || phone.isBlank()){
            throw new IllegalArgumentException(String.format("정확히 입력해주십시오"));
        }

        // 성별 확인
        int gender = Character.getNumericValue(ssn.charAt(7));
        if (gender % 2 == 0){
            gender = 0; // female
        } else {
            gender = 1; // male
        }

        this.uuid = UUID.randomUUID().toString();
        this.ssn = ssn;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.userType = userType;
        this.isManager = userType == 0;
    }

}
