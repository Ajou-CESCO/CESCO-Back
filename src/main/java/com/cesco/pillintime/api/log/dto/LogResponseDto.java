package com.cesco.pillintime.api.log.dto;

import lombok.Data;

import java.util.List;

@Data
public class LogResponseDto {

    List<Long> cabinetIndexList;
    List<LogDto> logList;

    public LogResponseDto(List<Long> cabinetIndexList, List<LogDto> logList) {
        this.cabinetIndexList = cabinetIndexList;
        this.logList = logList;
    }

}
