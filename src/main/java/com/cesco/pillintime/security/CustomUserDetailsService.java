package com.cesco.pillintime.security;

import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.entity.Member;
import com.cesco.pillintime.mapper.MemberMapper;
import com.cesco.pillintime.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        Member member = memberRepository.findByUuid(uuid).orElse(null);
        MemberDto memberDto = MemberMapper.INSTANCE.toDto(member);

        return new CustomUserDetails(memberDto);
    }
}
