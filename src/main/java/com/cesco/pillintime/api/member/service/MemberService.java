package com.cesco.pillintime.api.member.service;

import com.cesco.pillintime.api.member.repository.MemberRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.api.member.dto.MemberDto;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.mapper.MemberMapper;
import com.cesco.pillintime.security.JwtUtil;
import com.cesco.pillintime.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final SecurityUtil securityUtil;

    @Transactional
    public String createUser(MemberDto memberDto){

        String name = memberDto.getName();
        String ssn = memberDto.getSsn();
        String phone = memberDto.getPhone();
        boolean isManager = memberDto.isManager();

        // 회원가입 여부 확인
        memberRepository.findByPhone(phone)
                .ifPresent((member) -> {
                    throw new CustomException(ErrorCode.ALREADY_EXISTS_PHONE);
                });

        memberRepository.findBySsn(ssn)
                .ifPresent((member) -> {
                    throw new CustomException(ErrorCode.ALREADY_EXISTS_SSN);
                });

        // 회원가입 진행 Mapper로 왜 안바꿈?
        Member member = new Member(name, phone, ssn, isManager);
        memberRepository.save(member);

        return jwtUtil.createAccessToken(member);
    }

    public MemberDto getUserById(Long targetId) {

        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (targetId == null) {
            return MemberMapper.INSTANCE.toDto(requestMember);
        } else {
            Member targetMember = memberRepository.findById(targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

            if (securityUtil.checkPermission(requestMember, targetMember)) {
                return MemberMapper.INSTANCE.toDto(targetMember);
            }
        }

        return null;
    }

    @Transactional
    public MemberDto updateUserById(Long targetId, MemberDto memberDto) {

        String ssn = memberDto.getSsn();
        String name = memberDto.getName();
        String phone = memberDto.getPhone();

        Member targetMember;
        Member requestMember = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (targetId == null) {
            targetMember = requestMember;
        } else {
            targetMember = memberRepository.findById(targetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

            securityUtil.checkPermission(requestMember, targetMember);
        }

        targetMember.setSsn(ssn);
        targetMember.setName(name);
        targetMember.setPhone(phone);

        memberRepository.save(targetMember);
        return MemberMapper.INSTANCE.toDto(targetMember);
    }

    @Transactional
    public void deleteUser(){
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        memberRepository.delete(member);
    }

    // ============================================================================


}
