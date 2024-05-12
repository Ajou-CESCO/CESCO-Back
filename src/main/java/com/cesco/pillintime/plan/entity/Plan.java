package com.cesco.pillintime.plan.entity;

import com.cesco.pillintime.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne
    private Member member;

    @Column
    private Long medicineId;

    @Column
    private Integer weekday;

    @Column
    private String time;

    public Plan(Member member, Long medicineId, Integer weekday, String time) {
        this.member = member;
        this.medicineId = medicineId;
        this.weekday = weekday;
        this.time = time;
    }

}
