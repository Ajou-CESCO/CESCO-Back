package com.cesco.pillintime.api.plan.entity;

import com.cesco.pillintime.api.medicine.dto.MedicineDto;
import com.cesco.pillintime.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.time.LocalDate;
import java.time.LocalTime;
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column
    private String medicineId;

    @Column
    private String medicineName;

    @Column
    private String medicineSeries;

    @Column
    private Integer cabinetIndex;

    @Column
    private Integer weekday;

    @Column
    private LocalTime time;

    @Column
    private LocalDate startAt;

    @Column
    private LocalDate endAt;

    public Plan(Member member, MedicineDto medicineDto, Integer cabinetIndex, Integer weekday, LocalTime time, LocalDate startAt, LocalDate endAt) {
        this.member = member;
        this.medicineId = medicineDto.getMedicineCode();
        this.medicineName = medicineDto.getMedicineName();
        this.medicineSeries = medicineDto.getMedicineSeries();
        this.cabinetIndex = Objects.requireNonNullElse(cabinetIndex, 1);
        this.weekday = weekday;
        this.time = time;

        // 시작일이 null이면 오늘 날짜로 설정
        this.startAt = Objects.requireNonNullElseGet(startAt, LocalDate::now);

        // 종료일이 null이면 "2099-12-31"로 설정
        this.endAt = Objects.requireNonNullElseGet(endAt, () -> LocalDate.of(2099, 12, 31));
    }

    @Override
    public String toString() {
        return "Plan {" +
                "id=" + id +
                ", member=" + member.getName() +
                ", medicineId='" + medicineId + '\'' +
                ", medicineName='" + medicineName + '\'' +
                ", medicineSeries='" + medicineSeries + '\'' +
                ", cabinetIndex='" + cabinetIndex + '\'' +
                ", weekday=" + weekday +
                ", time='" + time + '\'' +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }

}
