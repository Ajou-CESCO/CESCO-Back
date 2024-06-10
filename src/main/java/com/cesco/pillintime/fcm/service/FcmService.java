package com.cesco.pillintime.fcm.service;

import com.cesco.pillintime.api.log.entity.Log;
import com.cesco.pillintime.api.log.repository.LogRepository;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.fcm.dto.FcmMessageDto;
import com.cesco.pillintime.fcm.dto.FcmRequestDto;
import com.cesco.pillintime.fcm.dto.FcmTokenDto;
import com.cesco.pillintime.security.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;
    private final SecurityUtil securityUtil;

    public void sendPushAlarm(FcmRequestDto fcmRequestDto, boolean checkMember) {
        Member targetMember = memberRepository.findById(fcmRequestDto.getTargetId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member requestMember;
        if (checkMember) {
            requestMember = SecurityUtil.getCurrentMember()
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

            securityUtil.checkPermission(requestMember, targetMember);
        } else {
            requestMember = null;
        }

        // 찌르기를 호출했을 경우
        if ((fcmRequestDto.getBody() == null) && requestMember != null) {
            fcmRequestDto.setTitle("[약속시간] \uD83D\uDC89 콕 찌르기 \uD83D\uDC89");
            fcmRequestDto.setBody(requestMember.getName() + " 님이 저를 찔렀어요");

            // 오늘의 미완료된 로그를 가져옴
            LocalDateTime now = LocalDateTime.now();
            logRepository.findUnfinishedLog(now.toLocalDate(), targetMember)
                    .ifPresent((logList) -> {
                        System.out.println(logList.size());
                        // 가장 근접한 로그의 plannedAt을 찾음
                        Optional<Log> nearestLog = logList.stream()
                                .min(Comparator.comparing(log -> Math.abs(log.getPlannedAt().until(now, ChronoUnit.SECONDS))));

                        nearestLog.ifPresent(nearest -> {
                            LocalDateTime nearestPlannedAt = nearest.getPlannedAt();
                            LocalTime plannedTime = LocalTime.of(nearestPlannedAt.getHour(), nearestPlannedAt.getMinute());

                            fcmRequestDto.setBody(requestMember.getName() + " 님이 " + plannedTime + "에 약을 먹지 않았다고 나를 찔렀어요");
                        });
                    });
        }

        try {
            String message = makeMessage(fcmRequestDto, targetMember);
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + getAccessToken());

            HttpEntity<String> entity = new HttpEntity<>(message, headers);

            String API_URL = "https://fcm.googleapis.com/v1/projects/pillintime-49253/messages:send";
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.FCM_SERVER_ERROR);
        }

    }

    public void sendFcmToken(FcmTokenDto fcmTokenDto) {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String fcmToken = fcmTokenDto.getFcmToken();
        member.setFcmToken(fcmToken);
        try {
            memberRepository.save(member);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // =================================================================

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/fcm-secret.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeMessage(FcmRequestDto fcmRequestDto, Member targetMember) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();

        String fcmToken = targetMember.getFcmToken();

        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(fcmToken)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(fcmRequestDto.getTitle())
                                .body(fcmRequestDto.getBody())
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        return om.writeValueAsString(fcmMessageDto);
    }

}
