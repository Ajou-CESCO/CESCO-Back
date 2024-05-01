package com.cesco.pillintime.mapper;

import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.entity.Member;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-01T15:51:56+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.7.jar, environment: Java 18.0.2 (Azul Systems, Inc.)"
)
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member toEntity(MemberDto memberDto) {
        if ( memberDto == null ) {
            return null;
        }

        Member member = new Member();

        member.setId( memberDto.getId() );
        member.setName( memberDto.getName() );
        member.setPhone( memberDto.getPhone() );
        member.setUuid( memberDto.getUuid() );
        member.setSsn( memberDto.getSsn() );
        member.setGender( memberDto.getGender() );
        member.setUserType( memberDto.getUserType() );

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
        memberDto.setUuid( member.getUuid() );
        memberDto.setUserType( member.getUserType() );

        return memberDto;
    }
}
