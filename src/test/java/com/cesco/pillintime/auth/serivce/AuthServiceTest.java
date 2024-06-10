package com.cesco.pillintime.auth.serivce;

import com.cesco.pillintime.CustomTestWatcher;
import com.cesco.pillintime.api.auth.dto.LoginDto;
import com.cesco.pillintime.api.auth.serivce.AuthService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.security.JwtUtil;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, CustomTestWatcher.class})
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private DefaultMessageService defaultMessageService;

    @InjectMocks
    private AuthService authService;

    private Member guardian;
    @BeforeEach
    void setUp() {
        guardian = new Member();
        guardian.setId(1L);
        guardian.setName("guardian");
        guardian.setSsn("123123-1");
        guardian.setPhone("010-1234-1234");
        guardian.setManager(false);

        // Set up values for properties annotated with @Value
        ReflectionTestUtils.setField(authService, "apiKey", "testApiKey");
        ReflectionTestUtils.setField(authService, "secretKey", "testSecretKey");
        ReflectionTestUtils.setField(authService, "from", "testFrom");
    }

    @Nested
    class 로그인 {
        @Test
        void 로그인() {
            // given
            LoginDto loginDto = new LoginDto();
            loginDto.setName(guardian.getName());
            loginDto.setPhone(guardian.getPhone());
            loginDto.setSsn(guardian.getSsn());

            when(memberRepository.findByNameAndPhoneAndSsn(any(), any(), any())).thenReturn(Optional.of(guardian));
            when(jwtUtil.createAccessToken(guardian)).thenReturn("testToken");

            // when
            String accessToken = authService.login(loginDto);

            // then
            assertEquals("testToken", accessToken);
            verify(memberRepository, times(1)).findByNameAndPhoneAndSsn(loginDto.getName(), loginDto.getPhone(), loginDto.getSsn());
            verify(jwtUtil, times(1)).createAccessToken(guardian);
        }
        @Test
        void 회원가입하지_않은_사용자() {
            // given
            LoginDto loginDto = new LoginDto();
            loginDto.setName(guardian.getName());
            loginDto.setPhone(guardian.getPhone());
            loginDto.setSsn(guardian.getSsn());

            when(memberRepository.findByNameAndPhoneAndSsn(any(), any(), any())).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                authService.login(loginDto);
            });
            // then
            assertEquals(ErrorCode.NOT_FOUND_USER, exception.getErrorCode());

            verify(memberRepository, times(1)).findByNameAndPhoneAndSsn(loginDto.getName(), loginDto.getPhone(), loginDto.getSsn());
            verify(jwtUtil, never()).createAccessToken(any());
        }
    }

    @Nested
    class 문자인증 {
        @Test
        void 문자인증() {

        }
        @Test
        void 예외_서버에러() {

        }
    }
}
