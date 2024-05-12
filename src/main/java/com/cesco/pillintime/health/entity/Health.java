package com.cesco.pillintime.health.entity;

import com.cesco.pillintime.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.sql.Time;
import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
public class Health {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer steps;

    @Column
    private double cal;

    @Column
    private LocalTime sleepTime;

    @OneToOne
    @JoinColumn
    private Member member;

    public Health(Integer steps, double cal, LocalTime sleepTime, Member member) {
        this.steps = steps;
        this.cal = cal;
        this.sleepTime = sleepTime;
        this.member = member;
    }
}