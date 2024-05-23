package com.cesco.pillintime.api.relation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public class RelationMapper {

    RelationMapper INSTANCE = Mappers.getMapper(RelationMapper.class);

}
