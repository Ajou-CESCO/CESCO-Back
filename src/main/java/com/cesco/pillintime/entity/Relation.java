package com.cesco.pillintime.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "relation")
@NoArgsConstructor
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name="MemberId")
    @ManyToOne
    private Member managerId;

    @JoinColumn(name="MemberId")
    @ManyToOne
    private Member clientId;

    public Relation(Member managerId, Member clientId) {
        this.managerId = managerId;
        this.clientId = clientId;
    }
}