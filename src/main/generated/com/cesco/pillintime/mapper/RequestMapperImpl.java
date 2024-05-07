package com.cesco.pillintime.mapper;

import com.cesco.pillintime.dto.RequestDto;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.entity.Request.RequestBuilder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-07T16:59:22+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 18.0.2 (Azul Systems, Inc.)"
)
public class RequestMapperImpl implements RequestMapper {

    @Override
    public Request toEntity(RequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        RequestBuilder request = Request.builder();

        request.senderId( requestDto.getSenderId() );
        request.receiverPhone( requestDto.getReceiverPhone() );

        return request.build();
    }

    @Override
    public RequestDto toDto(Request request) {
        if ( request == null ) {
            return null;
        }

        RequestDto requestDto = new RequestDto();

        requestDto.setSenderId( request.getSenderId() );
        requestDto.setReceiverPhone( request.getReceiverPhone() );

        return requestDto;
    }
}
