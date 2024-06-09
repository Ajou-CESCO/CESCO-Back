package com.cesco.pillintime.api.payment.entity;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.payment.dto.PaymentResDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private PayType payType;

    @Column
    private Long amount;

    @Column
    private String orderId;

    @Column
    private String orderName;

    @JoinColumn
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Column
    private boolean paySuccessYn;

    @Column
    private String paymentKey;

    @Column
    private String failReason;

    public PaymentResDto toDto() {
        return PaymentResDto.builder()
                .payType(payType.name())
                .amount(amount)
                .orderId(orderId)
                .orderName(orderName)
                .build();
    }

}
