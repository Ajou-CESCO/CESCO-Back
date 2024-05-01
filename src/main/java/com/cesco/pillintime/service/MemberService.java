package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void createUser(MemberDto memberDto){
        String ssn = memberDto.getSsn();
        String name = memberDto.getName();
        String phone = memberDto.getPhone();
        Integer userType = memberDto.getUserType();

        // 회원가입 여부 확인
        memberRepository.findByPhone(phone)
                .ifPresent((member) -> {
                    throw new CustomException(ErrorCode.ALREADY_EXISTS_PHONE);
                });

        // 회원가입 진행
        Member member = new Member(name, phone, ssn, userType);
        memberRepository.save(member);
    }

    // 내 정보 조회
    public Member getUserByUuid(String uuid) {
        try {
            /*
            토큰 검증 로직 필요
            사용자 토큰을 기반으로 유효한 사용자일 경우 해당 사용자 정보 반환

            매개변수로 uuid가 넘어올 경우, 해당 사용자와의 관계 체크 후 정보 반환
            없을 경우는 자기 자신의 정보 반환
             */

            // TODO
//            if (uuid.equals("")) {  // 내 정보 조회
//
//            } else {                // 연관된 사용자 정보 조회
//
//            }

            uuid = "1234"; // 토큰 내부 uuid 임시정의

            return memberRepository.findByUuid(uuid);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    // 정보 수정
    public Member updateUserByUuid(String uuid, MemberDto memberDto) {
        /*
        uuid 값 유무에 따라 내 정보 혹은 연관된 사용자 정보 수정

        uuid X -> 내 정보 수정
        uuid O -> 연관된 사용자 정보 수정. 즉 권한 체크 필요
         */

        Member member = memberRepository.findByUuid(uuid); // 토큰 검증 대신 사용

//        if (member == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is not exist");
//        }

        // 사용자 정보 업데이트
        member.setName(memberDto.getName());
        member.setSsn(memberDto.getSsn());
        member.setGender(memberDto.getGender());
        member.setPhone(memberDto.getPhone());

        // 사용자 정보 저장
        memberRepository.save(member);    // save, update 확인 필요
        return member;
    }

    // 탈퇴
    public void deleteUser(){
        /*
        헤더에 담어서 토큰을 담아서 보낸다.
         */
        try {
            String uuid = "1234";
            Member member = memberRepository.findByUuid(uuid); // 토큰 검증 대신 사용
//            if( member == null ) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
//            }
            memberRepository.delete(member);
        }
        catch (EntityNotFoundException ex) {
            // TODO -> 헨들러 연결
        }
    }

}
