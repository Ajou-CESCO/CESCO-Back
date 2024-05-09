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

    @JoinColumn(name="Manager")
    @ManyToOne
    private Member manager;

    @JoinColumn(name="Client")
    @ManyToOne
    private Member client;

    public Relation(Member manager, Member client) {
        this.manager = manager;
        this.client = client;
    }
}