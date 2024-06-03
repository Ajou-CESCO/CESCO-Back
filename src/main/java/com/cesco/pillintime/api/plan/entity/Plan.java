package com.cesco.pillintime.api.plan.entity;

import com.cesco.pillintime.api.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    @ElementCollection
    @CollectionTable(name = "medicine_adverse", joinColumns = @JoinColumn(name = "plan_id"))
    @MapKeyColumn(name = "adverse_key")
    @Column(name = "adverse_value")
    private Map<String, String> medicineAdverse;

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

    @Builder
    public Plan(
            Member member,
            String medicineId,
            String medicineName,
            String medicineSeries,
            Map<String, String> medicineAdverse,
            Integer cabinetIndex,
            Integer weekday,
            LocalTime time,
            LocalDate startAt,
            LocalDate endAt
    ) {
        this.member = member;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.medicineSeries = medicineSeries;
        this.medicineAdverse = medicineAdverse;
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
