package com.cesco.pillintime.util;

import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.relation.entity.Relation;
import com.cesco.pillintime.relation.repository.RelationRepository;
import com.cesco.pillintime.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class SecurityUtil {

    private final RelationRepository relationRepository;

    public SecurityUtil(RelationRepository relationRepository) {
        this.relationRepository = relationRepository;
    }

    public static Optional<Member> getCurrentMember() {
        CustomUserDetails userDetails = getUserDetails();
        return userDetails.getMember();
    }

    public static Long getCurrentMemberId() {
        CustomUserDetails userDetails = getUserDetails();
        return userDetails.getId();
    }

    public static String getCurrentMemberPhone() {
        CustomUserDetails userDetails = getUserDetails();
        return userDetails.getPhone();
    }

    private static CustomUserDetails getUserDetails() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new CustomException(ErrorCode.TOKEN_IS_INVALID);
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

    public boolean checkPermission(Member requestMember, Member targetMember) {
        /*
         * 요청한 사용자가 목표로 하는 사용자에 접근 권한이 있는지 확인하기 위한 함수
         * 생성된 연관 관계가 없을 경우 에러 반환
         */

        List<Relation> relationList = relationRepository.findByMember(requestMember)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_USER_ACCESS));

        for (Relation relation : relationList) {
            if ((relation.getClient().equals(targetMember))
                    || (relation.getManager().equals(targetMember))) {
                return true;
            }
        }

        throw new CustomException(ErrorCode.INVALID_USER_ACCESS);
    }

}
