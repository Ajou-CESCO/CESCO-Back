package com.cesco.pillintime.mapper;

import com.cesco.pillintime.dto.RelationDto;
import com.cesco.pillintime.entity.Relation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public class RelationMapper {

    RelationMapper INSTANCE = Mappers.getMapper(RelationMapper.class);

}
