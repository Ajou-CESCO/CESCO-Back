package com.cesco.pillintime.cabinet.entity;

import com.cesco.pillintime.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Cabinet {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name="memberId")
    @ManyToOne
    private Member ownerId;

    public Cabinet(String uuid, Member member) {
        this.uuid = uuid;
        this.ownerId = member;
    }
}
