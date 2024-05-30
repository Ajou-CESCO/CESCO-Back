package com.cesco.pillintime.api.member.mapper;

import com.cesco.pillintime.api.member.dto.MemberDto;
import com.cesco.pillintime.api.member.entity.Member;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-30T15:47:20+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
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

        memberDto.setCabinetId( member.getCabinet() != null ? member.getCabinet().getId() : 0 );

        return memberDto;
    }
}
