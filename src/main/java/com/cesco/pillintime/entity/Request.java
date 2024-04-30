package com.cesco.pillintime.entity;

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
    private String receiverName;

    @Column
    private String receiverNumber;

    @Column
    private String receiverSsn;

    @Builder
    public Request(Long senderId, String receiverName, String receiverNumber, String receiverSsn) {
        this.senderId = senderId;
        this.receiverName = receiverName;
        this.receiverNumber = receiverNumber;
        this.receiverSsn = receiverSsn;
    }

}