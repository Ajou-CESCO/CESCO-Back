package com.cesco.pillintime.log.entity;

import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.plan.entity.Plan;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Entity
@NoArgsConstructor
public class Log {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne
    private Member member;

    @JoinColumn
    @ManyToOne
    private Plan plan;

    @Column
    private LocalDate plannedAt;

    @Column
    private TakenStatus takenStatus;

}
