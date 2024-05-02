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

    @Column
    private Long managerId;

    @Column
    private Long clientId;

    public Relation(Long managerId, Long clientId) {
        this.managerId = managerId;
        this.clientId = clientId;
    }
}