package com.cesco.pillintime.mapper;

import com.cesco.pillintime.dto.MemberDto;
import com.cesco.pillintime.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    Member toEntity(MemberDto memberDto);

    MemberDto toDto(Member member);
}
