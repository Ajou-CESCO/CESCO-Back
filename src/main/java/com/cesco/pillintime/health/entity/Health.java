package com.cesco.pillintime.health.entity;

import com.cesco.pillintime.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@Entity
@NoArgsConstructor
public class Health {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private Integer steps;

    @Column
    private double cal;

    @Column
    private Time sleepTime;

    @Column
    private int weekday;

    @JoinColumn(name="memberId")
    @OneToOne
    private Member member;

    public Health(Integer steps, double cal, Time sleepTime, int weekday, Member member) {
        this.steps = steps;
        this.cal = cal;
        this.sleepTime = sleepTime;

        this.member = member;
    }
}