package com.cesco.pillintime.plan.entity;

import com.cesco.pillintime.medicine.dto.MedicineDto;
import com.cesco.pillintime.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.Objects;

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
    private String medicineId;

    @Column
    private String medicineName;

    @Column
    private Integer weekday;

    @Column
    private String time;

    @Column
    private LocalDate startedAt;

    @Column
    private LocalDate endedAt;

    public Plan(Member member, MedicineDto medicineDto, Integer weekday, String time, LocalDate startedAt, LocalDate endedAt) {
        this.member = member;
        this.medicineId = medicineDto.getMedicineCode();
        this.medicineName = medicineDto.getMedicineName();
        this.weekday = weekday;
        this.time = time;

        // 시작일이 null이면 오늘 날짜로 설정
        this.startedAt = Objects.requireNonNullElseGet(startedAt, LocalDate::now);

        // 종료일이 null이면 "2099-12-31"로 설정
        this.endedAt = Objects.requireNonNullElseGet(endedAt, () -> LocalDate.of(2099, 12, 31));
    }

}
