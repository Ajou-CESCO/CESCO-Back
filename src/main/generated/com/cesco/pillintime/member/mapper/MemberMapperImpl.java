package com.cesco.pillintime.member.mapper;

import com.cesco.pillintime.member.dto.MemberDto;
import com.cesco.pillintime.member.entity.Member;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-12T22:56:56+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 18.0.2 (Amazon.com Inc.)"
)
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toEntity(MemberDto memberDto) {
        if ( memberDto == null ) {
            return null;
        }

        Member member = new Member();

        member.setSsn( memberDto.getSsn() );
        member.setId( memberDto.getId() );
        member.setName( memberDto.getName() );
        member.setPhone( memberDto.getPhone() );
        member.setGender( memberDto.getGender() );
        member.setManager( memberDto.isManager() );
        member.setSubscriber( memberDto.isSubscriber() );
        member.setHasCase( memberDto.isHasCase() );

        return member;
    }

    @Override
    public MemberDto toDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberDto memberDto = new MemberDto();

        memberDto.setId( member.getId() );
        memberDto.setName( member.getName() );
        memberDto.setSsn( member.getSsn() );
        memberDto.setPhone( member.getPhone() );
        memberDto.setGender( member.getGender() );
        memberDto.setManager( member.isManager() );
        memberDto.setSubscriber( member.isSubscriber() );
        memberDto.setHasCase( member.isHasCase() );

        return memberDto;
    }
}
