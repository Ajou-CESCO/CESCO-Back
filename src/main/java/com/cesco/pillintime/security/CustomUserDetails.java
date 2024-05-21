package com.cesco.pillintime.security;

import com.cesco.pillintime.cabinet.entity.Cabinet;
import com.cesco.pillintime.cabinet.repository.CabinetRepository;
import com.cesco.pillintime.exception.CustomException;
import com.cesco.pillintime.exception.ErrorCode;
import com.cesco.pillintime.member.dto.MemberDto;
import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.member.mapper.MemberMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final MemberDto memberDto;
    private final CabinetRepository cabinetRepository;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_" + "NORMAL");

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        try {
            return memberDto.getName();
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<Member> getMember() {
        Member member = MemberMapper.INSTANCE.toEntity(memberDto);

        Long cabinetId = memberDto.getCabinetId();
        if (cabinetId != null && cabinetId != 0) {
            Cabinet cabinet = cabinetRepository.findById(cabinetId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CABINET));

            member.setCabinet(cabinet);
            return Optional.of(member);
        }

        return Optional.of(member);
    }

    public Long getId() {
        return memberDto.getId();
    }

    public String getPhone() {
        return memberDto.getPhone();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
