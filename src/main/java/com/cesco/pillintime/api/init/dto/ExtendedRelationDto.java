package com.cesco.pillintime.api.init.dto;

import com.cesco.pillintime.api.relation.dto.RelationDto;
import lombok.Data;

import java.util.List;

@Data
public class ExtendedRelationDto {

    private Long id;
    private Long memberId;
    private String memberName;
    private String memberSsn;
    private String memberPhone;
    private Long cabinetId;
    private List<Long> cabinetIndexList;

    public ExtendedRelationDto(RelationDto relation, List<Long> cabinetIndexList) {
        this.id = relation.getId();
        this.memberId = relation.getMemberId();
        this.memberName = relation.getMemberName();
        this.memberSsn = relation.getMemberSsn();
        this.memberPhone = relation.getMemberPhone();
        this.cabinetId = relation.getCabinetId();
        this.cabinetIndexList = cabinetIndexList;
    }
}
