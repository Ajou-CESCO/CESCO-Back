package com.cesco.pillintime.security;

import com.cesco.pillintime.api.cabinet.repository.CabinetRepository;
import com.cesco.pillintime.api.member.dto.MemberDto;
import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.member.mapper.MemberMapper;
import com.cesco.pillintime.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final CabinetRepository cabinetRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Long longId = Long.valueOf(id);

        Member member = memberRepository.findById(longId).orElse(null);
        MemberDto memberDto = MemberMapper.INSTANCE.toDto(member);

        return new CustomUserDetails(memberDto, cabinetRepository);
    }
}
