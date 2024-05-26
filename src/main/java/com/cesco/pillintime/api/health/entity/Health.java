package com.cesco.pillintime.api.health.entity;

import com.cesco.pillintime.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @ManyToOne
    @JoinColumn
    private Member member;

    @Column
    private LocalDateTime lastUpLoadTime = LocalDateTime.now();

    public Health(Integer steps, double cal, LocalTime sleepTime, Member member) {
        this.steps = steps;
        this.cal = cal;
        this.sleepTime = sleepTime;
        this.member = member;
    }
}