package com.cesco.pillintime.relation.entity;

import com.cesco.pillintime.member.entity.Member;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Member manager;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Member client;

    public Relation(Member manager, Member client) {
        this.manager = manager;
        this.client = client;
    }
}