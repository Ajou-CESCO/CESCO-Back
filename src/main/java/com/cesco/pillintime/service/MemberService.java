package com.cesco.pillintime.service;

import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.entity.Relation;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.mapper.MemberMapper;
import com.cesco.pillintime.repository.CabinetRepository;
import com.cesco.pillintime.repository.HealthRepository;
import com.cesco.pillintime.repository.MemberRepository;
import com.cesco.pillintime.repository.RelationRepository;
import com.cesco.pillintime.util.JwtUtil;
import com.cesco.pillintime.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RelationRepository relationRepository;
    private final CabinetRepository cabinetRepository;
    private final HealthRepository healthRepository;

    private final JwtUtil jwtUtil;

    public String createUser(MemberDto memberDto){

        String name = memberDto.getName();
        String ssn = memberDto.getSsn();
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

        // 생성된 uuid를 추가하여 토큰 반환
        memberDto.setUuid(member.getUuid());
        return jwtUtil.createAccessToken(memberDto);
    }

    public MemberDto getUserByUuid(String uuid) {
        /*
        uuid 값 유무에 따라 내 정보 혹은 연관된 사용자 정보 조회

        uuid X -> 내 정보 조회
        uuid O -> 연관된 사용자 정보 조회. 즉 권한 체크 필요
         */

        Long id = SecurityUtil.getCurrentMemberId();
        System.out.print(id);

        Member requestUser = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (uuid.isEmpty()) {   // 본인 정보
            return MemberMapper.INSTANCE.toDto(requestUser);
        } else {                // 타인 정보
            /* TODO - relation 기반 검증 로직 필요
              토큰에 있는 API를 호출한 사용자의 PK 가져오기
              -> Relation의 mangerid == PK 인 모든 데이터 가져오기
              -> 이 모든 데이터들 중 내가 조회할 사용자 uuid가 있는지
              -> 해당 데이터가 있다면 허용
              -> 해당 데이터가 없다면 403(Forbidden) 에러 발생
             */
            Member targetUser = memberRepository.findByUuid(uuid);

            List<Relation> relationList = relationRepository.findByMemberId(id);
            if (relationList == null) {
                return null;
            }

            for (Relation relation : relationList) {
                if (relation.getClient().getId().equals(targetUser.getId()) ||
                        (relation.getManager().getId().equals(targetUser.getId()))) {
                    return MemberMapper.INSTANCE.toDto(targetUser);
                }
            }

            throw new CustomException(ErrorCode.INVALID_USER_ACCESS);
        }

    }

    public MemberDto updateUserByUuid(String uuid, MemberDto memberDto) {
        /*
        uuid 값 유무에 따라 내 정보 혹은 연관된 사용자 정보 수정

        uuid X -> 내 정보 수정
        uuid O -> 연관된 사용자 정보 수정. 즉 권한 체크 필요
         */

        Long id = SecurityUtil.getCurrentMemberId();

        if (uuid.isEmpty()) {
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

            member.setSsn(memberDto.getSsn());
            member.setName(memberDto.getName());
            member.setPhone(memberDto.getPhone());

            memberRepository.save(member);
            return MemberMapper.INSTANCE.toDto(member);

        } else {                // 타인 정보
            Member requester = memberRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

            Member target = memberRepository.findByUuid(uuid);

            List<Relation> relationlist = relationRepository.findByMemberId(id);

            if (relationlist != null) {
                for(Relation relation : relationlist) {

                    Member member = requester.getUserType() == 0 ? relation.getClient() : relation.getManager();

                    if (member.getId().equals(target.getId())){
                        target.setSsn(memberDto.getSsn());
                        target.setName(memberDto.getName());
                        target.setPhone(memberDto.getPhone());
                        memberRepository.save(target);
                        return MemberMapper.INSTANCE.toDto(target);
                    }
                }
            }
            throw new CustomException(ErrorCode.INVALID_USER_ACCESS);
        }
    }

    public void deleteUser(){

        Long id = SecurityUtil.getCurrentMemberId();

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        relationRepository.deleteByMemberId(member.getId());
//        cabinetRepository.deleteByOwnerId(member.getId());
        healthRepository.deleteByOwnerId(member.getId());

        memberRepository.delete(member);
    }

}
