package com.cesco.pillintime.api.member.mapper;

import com.cesco.pillintime.api.member.dto.MemberDto;
import com.cesco.pillintime.api.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    @Mapping(target = "cabinet", ignore = true)
    Member toEntity(MemberDto memberDto);

    @Mapping(target = "cabinetId", expression = "java(member.getCabinet() != null ? member.getCabinet().getId() : 0)")
    MemberDto toDto(Member member);
}
