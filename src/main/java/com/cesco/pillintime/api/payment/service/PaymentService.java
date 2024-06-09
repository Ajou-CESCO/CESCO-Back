package com.cesco.pillintime.api.payment.service;

import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.payment.dto.PaymentFailDto;
import com.cesco.pillintime.api.payment.dto.PaymentReqDto;
import com.cesco.pillintime.api.payment.dto.PaymentResDto;
import com.cesco.pillintime.api.payment.entity.Payment;
import com.cesco.pillintime.api.payment.repository.PaymentRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    @Value("${TOSS_TEST_CLIENT_API_KEY}")
    private String testClientApiKey;

    @Value("${TOSS_TEST_SECRET_API_KEY}")
    private String testSecretApiKey;

    @Value("${TOSS_SUCCESS_URL}")
    private String successCallBackUrl;

    @Value("${TOSS_FAIL_URL}")
    private String failCallBackUrl;

    @Transactional
    public PaymentResDto getPaymentInfo(PaymentReqDto paymentReqDto) {
        Long amount = paymentReqDto.getAmount();
        String payType = paymentReqDto.getPayType().name();
        Long memberId = paymentReqDto.getMemberId();
        String orderName = paymentReqDto.getOrderName();

        if (!orderName.equals("프리미엄 이용권")) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_NAME);
        }

        if (amount != 30000) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        PaymentResDto paymentResDto;
        try {
            Payment payment = paymentReqDto.toEntity();
            memberRepository.findById(memberId)
                    .ifPresent((member) -> {
                            payment.setMember(member);
                            paymentRepository.save(payment);
                        }
                    );

            paymentResDto = payment.toDto();
            paymentResDto.setSuccessUrl(successCallBackUrl);
            paymentResDto.setFailUrl(failCallBackUrl);
            return paymentResDto;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public String getPaymentFinalInfo(String paymentKey, String orderId, Long amount) {
        // 사용자가 요청한 Payment 검증
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT));

        if (Objects.equals(payment.getAmount(), amount)) {
            payment.setPaymentKey(paymentKey);
        } else {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        // 정상적인 결제 기록일 경우 실제 결제 요청
        RestTemplate rest = new RestTemplate();

        testSecretApiKey = testSecretApiKey + ":";
        String encodedAuth = new String(Base64.getEncoder().encode(testSecretApiKey.getBytes(StandardCharsets.UTF_8)));

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(encodedAuth);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        JSONObject param = new JSONObject();
        param.put("orderId", orderId);
        param.put("amount", amount);

        return rest.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey,
                new HttpEntity<>(param, headers),
                String.class
        ).getBody();
    }

    public PaymentFailDto getPaymentFailInfo(String errorCode, String errorMsg, String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PAYMENT));

        payment.setPaySuccessYn(false);
        payment.setFailReason(errorMsg);
        paymentRepository.save(payment);

        return PaymentFailDto.builder()
                .errorCode(errorCode)
                .errorMsg(errorMsg)
                .orderId(orderId)
                .build();
    }
}
