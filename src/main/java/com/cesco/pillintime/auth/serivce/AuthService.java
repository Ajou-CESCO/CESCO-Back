package com.cesco.pillintime.auth.serivce;

import com.cesco.pillintime.auth.dto.LoginDto;
import com.cesco.pillintime.auth.dto.SmsDto;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private DefaultMessageService messageService;
    private final JwtUtil jwtUtil;

    @Value("${COOLSMS_API_KEY}")
    private String apiKey;

    @Value("${COOLSMS_SECRET_KEY}")
    private String secretKey;

    @Value("${COOLSMS_FROM}")
    private String from;

    @Transactional
    public String login(LoginDto loginDto) {
        String name = loginDto.getName();
        String phone = loginDto.getPhone();
        String ssn = loginDto.getSsn();

        Member member = memberRepository.findByNameAndPhoneAndSsn(name, phone, ssn)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        return jwtUtil.createAccessToken(member);
    }

    public String getSmsCode(SmsDto smsDto) {
        String to = smsDto.getPhone();
        String formattedTo = to.replace("-", "");

        String verficationCode = generateRandomCode(6);

        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, secretKey, "https://api.coolsms.co.kr");

        Message message = new Message();
        message.setFrom(from);
        message.setTo(formattedTo);
        message.setText("[약속시간] [" +  verficationCode + "] 인증번호를 화면에 입력해주세요");

        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        if (response != null && !response.getStatusCode().equals("2000")) {
            throw new CustomException(ErrorCode.EXTERNAL_API_ERROR);
        } else {
            return verficationCode;
        }
    }

    // =============================================================

    private String generateRandomCode(int length) {
        String chars = "0123456789";
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

}
