package com.cesco.pillintime.mapper;

import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.dto.RequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RequestMapper {

    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

//    @Mapping(target = "id", ignore = true) // ID 필드를 무시하도록 설정
    Request toEntity(RequestDto requestDto);

    RequestDto toDto(Request request);
}

