package com.cesco.pillintime.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

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

    @JoinColumn(name="memberId")
    @OneToOne
    private Member OwnerId;

    @Column
    private int weekday;

    public Health(Integer steps, double cal, Time sleepTime, int weekday, Member ownerId) {
        this.steps = steps;
        this.cal = cal;
        this.sleepTime = sleepTime;
        this.weekday = weekday;
        OwnerId = ownerId;
    }
}