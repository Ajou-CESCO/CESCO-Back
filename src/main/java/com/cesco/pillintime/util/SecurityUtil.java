package com.cesco.pillintime.util;

import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtil {

    public static String getCurrentMemberName() {
        CustomUserDetails userDetails = getUserDetails();
        return userDetails.getUsername();
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
            throw new RuntimeException("No authentication information");
        }

        return (CustomUserDetails) authentication.getPrincipal();
    }

}
