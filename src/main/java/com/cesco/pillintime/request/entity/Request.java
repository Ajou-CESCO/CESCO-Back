package com.cesco.pillintime.request.entity;

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

    @Column
    private Long senderId;

    @Column
    private String receiverPhone;

    @Builder
    public Request(Long senderId, String receiverPhone) {
        this.senderId = senderId;
        this.receiverPhone = receiverPhone;
    }

}