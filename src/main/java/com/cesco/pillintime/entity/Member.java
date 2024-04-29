package com.cesco.pillintime.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "member")
public class Member {

    @Id
    @Column
    @JoinColumn(name="memberId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column
    private String phone;

    @Column
    private String uuid;

    @Column(unique = true)
    private String ssn;

    @Column
    private Integer gender;

    @Column(name = "user_type")
    private Integer userType;

    @Column(name = "is_subscriber")
    private boolean isSubscriber = false;

    @Column(name = "has_case")
    private boolean hasCase = false;

    public Member(String name, String phone, String ssn, Integer userType){

        if(name == null || ssn == null || phone == null || name.isBlank() || ssn.isBlank() || phone.isBlank()){
            throw new IllegalArgumentException(String.format("정확히 입력해주십시오"));
        }

        // 성별 확인
        Integer gender = Character.getNumericValue(ssn.charAt(7));
        if (gender % 2 == 0){
            gender = 0; // female
        } else {
            gender = 1; // male
        }

        this.name = name;
        this.ssn = ssn;
        this.phone = phone;
        this.gender = gender;
        this.userType = userType;
    }

}
