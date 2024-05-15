package com.cesco.pillintime.log.service;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.log.dto.LogDto;
import com.cesco.pillintime.log.entity.Log;
import com.cesco.pillintime.log.mapper.LogMapper;
import com.cesco.pillintime.log.repository.LogRepository;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.repository.MemberRepository;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final MemberRepository memberRepository;

    public List<LogDto> getDoseLogByMemberId(LogDto inputLogDto) {
        Long targetId = inputLogDto.getMemberId();

        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        Member targetMember = memberRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (!requestMember.equals(targetMember)) {
            SecurityUtil.checkPermission(requestMember, targetMember);
        } else {
            targetMember = requestMember;
        }

        Optional<List<Log>> logListOptional = logRepository.findByMember(targetMember);
        List<LogDto> logDtoList = new ArrayList<>();

        logListOptional.ifPresent(logs -> {
            for (Log log : logs) {
                LogDto logDto = LogMapper.INSTANCE.toDto(log);
                logDtoList.add(logDto);
            }
        });

        return logDtoList;
    }

}
