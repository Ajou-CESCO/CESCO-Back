package com.cesco.pillintime.request.entity;

import com.cesco.pillintime.member.entity.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Request {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Member sender;

    @Column
    private String receiverPhone;

    @Builder
    public Request(Member sender, String receiverPhone) {
        this.sender = sender;
        this.receiverPhone = receiverPhone;
    }

}