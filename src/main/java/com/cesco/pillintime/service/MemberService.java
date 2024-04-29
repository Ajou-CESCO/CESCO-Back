package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.entity.Member;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final com.cesco.pillintime.repository.user.MemberRepository memberRepository;

    public MemberService(com.cesco.pillintime.repository.user.MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 회원 가입
    public void createUser(MemberDto memberDto){
        String name = memberDto.getName();
        String ssn = memberDto.getSsn();
        String phone = memberDto.getPhone();
        Integer userType = memberDto.getUserType();

        // 회원가입 여부 확인
        Member member = memberRepository.findByPhone(phone);
//        if (member != null) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
//        }

        // 회원가입 진행
        Member newMember = new Member(name, phone, ssn, userType);
        memberRepository.save(newMember);
    }

    // 로그인
    public ResponseEntity<String> joinUser(MemberDto request){
        try {
            // 회원 여부 확인
            Member member = memberRepository.findByNameAndSsnAndPhone(request.getName(), request.getSsn(), request.getPhone());
//            userRepository.findByNameAndSsnAndPhone(request.getName(), request.getSsn(), request.getPhone());
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            } else {
                // JWT TOKEN 생성
                return ResponseEntity.status(HttpStatus.OK).body("Success create plan");
            }
        }
        catch (EntityNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
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
