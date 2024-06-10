package com.cesco.pillintime.cabinet.service;

import com.cesco.pillintime.CustomTestWatcher;
import com.cesco.pillintime.api.cabinet.dto.CabinetDto;
import com.cesco.pillintime.api.cabinet.entity.Cabinet;
import com.cesco.pillintime.api.cabinet.repository.CabinetRepository;
import com.cesco.pillintime.api.cabinet.service.CabinetService;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.api.relation.entity.Relation;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.security.CustomUserDetails;
import com.cesco.pillintime.security.SecurityUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@ExtendWith(CustomTestWatcher.class)
class CabinetServiceTest {
    @Mock
    private CabinetRepository cabinetRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private CabinetService cabinetService;

    private Member guardian;
    private Member patient;
    private Cabinet cabinet;
    @BeforeEach
    void init() {
        guardian = new Member();
        guardian.setId(1L);
        guardian.setName("guardian");
        guardian.setSsn("123123-1");
        guardian.setPhone("010-1234-1234");
        guardian.setManager(false);

        patient = new Member();
        patient.setId(2L);
        patient.setName("patient");
        patient.setSsn("789789-2");
        patient.setPhone("010-5678-5678");
        patient.setManager(true);

        cabinet = new Cabinet();
        cabinet.setId(1L);
        cabinet.setSerial("abcdefghijklmnopqrstuvwxyz");

        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        lenient().when(authentication.getName()).thenReturn("guardian");
        lenient().when(authentication.getPrincipal()).thenReturn(mock(CustomUserDetails.class));
    }

    @Nested
    class 약통생성 {
        @Test
        void 피보호자_약통생성() {
            // given

            CabinetDto cabinetDto = new CabinetDto();
            cabinetDto.setSerial(cabinet.getSerial());
            cabinetDto.setOwnerId(patient.getId());

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(patient));
            when(cabinetRepository.findBySerial(cabinetDto.getSerial())).thenReturn(Optional.ofNullable(cabinet));

            // when
            cabinetService.createCabinet(cabinetDto);

            // then
            verify(cabinetRepository, times(1)).findBySerial(cabinetDto.getSerial());
            verify(cabinetRepository, times(1)).save(cabinet);
            verify(memberRepository, times(1)).save(patient);
        }
        @Test
        void 보호자_약통생성() {
            // given
            CabinetDto cabinetDto = new CabinetDto();
            cabinetDto.setSerial(cabinet.getSerial());
            cabinetDto.setOwnerId(patient.getId());

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(cabinetDto.getOwnerId())).thenReturn(Optional.of(patient));
            when(securityUtil.checkPermission(guardian, patient)).thenReturn(true);
            when(cabinetRepository.findBySerial(cabinetDto.getSerial())).thenReturn(Optional.ofNullable(cabinet));

            // when
            cabinetService.createCabinet(cabinetDto);

            // then
            verify(cabinetRepository, times(1)).findBySerial(cabinetDto.getSerial());
            verify(cabinetRepository, times(1)).save(cabinet);
            verify(memberRepository, times(1)).save(patient);
        }
        @Test
        void 예외_이미_생성된_약통() {
            // given
            cabinet.setOwner(patient);
            CabinetDto cabinetDto = new CabinetDto();
            cabinetDto.setSerial(cabinet.getSerial());
            cabinetDto.setOwnerId(patient.getId());

            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(memberRepository.findById(cabinetDto.getOwnerId())).thenReturn(Optional.of(patient));
            when(securityUtil.checkPermission(guardian, patient)).thenReturn(true);
            when(cabinetRepository.findBySerial(cabinetDto.getSerial())).thenReturn(Optional.ofNullable(cabinet));

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                cabinetService.createCabinet(cabinetDto);
            });

            // then
            Assertions.assertEquals(ErrorCode.ALREADY_EXISTS_OWNER ,exception.getErrorCode());
            verify(memberRepository, times(1)).findById(cabinetDto.getOwnerId());
            verify(securityUtil, times(1)).checkPermission(guardian, patient);
            verify(cabinetRepository, times(1)).findBySerial(cabinetDto.getSerial());
            verify(cabinetRepository, times(0)).save(cabinet);
            verify(memberRepository, times(0)).save(patient);
        }
    }
    @Nested
    class 약통삭제 {
        @Test
        void 피보호자_약통삭제() {
            // given
            cabinet.setOwner(patient);
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(patient));
            when(cabinetRepository.findById(cabinet.getId())).thenReturn(Optional.ofNullable(cabinet));

            // when
            cabinetService.deleteCabinet(cabinet.getId());

            // then
            verify(cabinetRepository, times(1)).findById(cabinet.getId());
            verify(cabinetRepository, times(1)).save(cabinet);
            verify(memberRepository, times(1)).save(patient);
        }
        @Test
        void 보호자_약통삭제() {
            // given
            cabinet.setOwner(patient);
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(cabinetRepository.findById(cabinet.getId())).thenReturn(Optional.ofNullable(cabinet));
            when(securityUtil.checkPermission(guardian, patient)).thenReturn(true);

            // when
            cabinetService.deleteCabinet(cabinet.getId());

            // then
            verify(cabinetRepository, times(1)).findById(cabinet.getId());
            verify(securityUtil, times(1)).checkPermission(guardian, patient);
            verify(cabinetRepository, times(1)).save(cabinet);
            verify(memberRepository, times(1)).save(patient);
        }
        @Test
        void 예외_이미_삭제된_약통() {
            // given
            when(SecurityUtil.getCurrentMember()).thenReturn(Optional.of(guardian));
            when(cabinetRepository.findById(cabinet.getId())).thenReturn(Optional.empty());

            // when
            CustomException exception = assertThrows(CustomException.class, () -> {
                cabinetService.deleteCabinet(cabinet.getId());
            });

            // then
            Assertions.assertEquals(ErrorCode.NOT_FOUND_CABINET, exception.getErrorCode());
            verify(cabinetRepository, times(1)).findById(cabinet.getId());
        }
    }
}