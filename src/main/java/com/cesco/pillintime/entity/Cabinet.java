package com.cesco.pillintime.entity;

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

    @Column(unique = true)
    private String uuid;

    @JoinColumn(name="memberId")
    @ManyToOne
    private Member ownerId;

    public Cabinet(String uuid, Member member) {
        this.uuid = uuid;
        this.ownerId = member;
    }
}
