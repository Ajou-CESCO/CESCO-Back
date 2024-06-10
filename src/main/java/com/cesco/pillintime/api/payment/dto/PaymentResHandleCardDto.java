package com.cesco.pillintime.api.payment.dto;

import lombok.Data;

@Data
public class PaymentResHandleCardDto {

    String company;
    String number;
    String installmentPlanMonths;
    String isInterestFree;
    String approveNo;
    String useCardPoint;
    String cardType;
    String ownerType;
    String acquireStatus;
    String receiptUrl;

}
