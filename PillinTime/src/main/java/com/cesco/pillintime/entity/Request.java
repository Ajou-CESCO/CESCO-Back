package com.cesco.pillintime.entity;

import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Request {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private long senderId;

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
