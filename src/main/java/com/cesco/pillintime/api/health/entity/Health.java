package com.cesco.pillintime.api.health.entity;

import com.cesco.pillintime.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
public class Health {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long steps;

    @Column
    private Long calorie;

    @Column
    private Long heartRate;

    @Column
    private Long sleepTime;

    @ManyToOne
    @JoinColumn
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public Health(Long steps, Long calorie, Long heartRate, Long sleepTime, Member member) {
        this.steps = steps;
        this.calorie = calorie;
        this.heartRate = heartRate;
        this.sleepTime = sleepTime;
        this.member = member;
    }
}