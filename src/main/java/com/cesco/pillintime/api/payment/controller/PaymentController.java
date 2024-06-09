package com.cesco.pillintime.api.payment.controller;

import com.cesco.pillintime.api.payment.dto.PaymentFailDto;
import com.cesco.pillintime.api.payment.dto.PaymentReqDto;
import com.cesco.pillintime.api.payment.dto.PaymentResDto;
import com.cesco.pillintime.api.payment.service.PaymentService;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.response.dto.ResponseDto;
import com.cesco.pillintime.response.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/toss")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ResponseDto> getPaymentInfo(@RequestBody PaymentReqDto paymentReqDto) {
        PaymentResDto paymentResDto = paymentService.getPaymentInfo(paymentReqDto);
        return ResponseUtil.makeResponse(200, "Success get payment info", paymentResDto);
    }

    @GetMapping("/success")
    public ResponseEntity<ResponseDto> getPaymentFinalInfo(
            @RequestParam String paymentKey, @RequestParam String orderId, @RequestParam Long amount) {
        try {
            String body = paymentService.getPaymentFinalInfo(paymentKey, orderId, amount);
            return ResponseUtil.makeResponse(200, "Success get payment info", body);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.NOT_FOUND_PAYMENT);
        }
    }

    @GetMapping("/fail")
    public ResponseEntity<ResponseDto> getPaymentFailInfo(
            @RequestParam String errorCode, @RequestParam String errorMsg, @RequestParam String orderId) {
        try {
            PaymentFailDto paymentFailDto = paymentService.getPaymentFailInfo(errorCode, errorMsg, orderId);
            return ResponseUtil.makeResponse(200, "Success get fail info", paymentFailDto);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
