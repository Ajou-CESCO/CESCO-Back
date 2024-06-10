package com.cesco.pillintime.api.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResDto {

    private String payType;
    private Long amount;
    private String orderId;
    private String orderName;
    private String successUrl;
    private String failUrl;
//    private String paySuccessYn;

}
