package com.cesco.pillintime.api.payment.dto;

import com.cesco.pillintime.api.payment.entity.PayType;
import com.cesco.pillintime.api.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReqDto {

    private PayType payType = PayType.valueOf("CARD");
    private Long amount = 30000L;
    private String orderName = "프리미엄 이용권";
    private Long memberId;

    public Payment toEntity() {
        return Payment.builder()
                .payType(payType)
                .amount(amount)
                .orderId(UUID.randomUUID().toString())
                .orderName(orderName)
                .paySuccessYn(false)
                .build();

    }

}
