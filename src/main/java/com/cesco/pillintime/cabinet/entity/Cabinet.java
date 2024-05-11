package com.cesco.pillintime.cabinet.entity;

import com.cesco.pillintime.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Cabinet {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String serial;

    @JoinColumn(name="memberId")
    @ManyToOne
    private Member owner;

    public Cabinet(String serial, Member member) {
        this.serial = serial;
        this.owner = member;
    }
}
