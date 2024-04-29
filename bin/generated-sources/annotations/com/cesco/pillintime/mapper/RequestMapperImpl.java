package com.cesco.pillintime.mapper;

import com.cesco.pillintime.dto.RequestDto;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.entity.Request.RequestBuilder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-25T17:14:24+0900",
    comments = "version: 1.4.2.Final, compiler: Eclipse JDT (IDE) 3.38.0.v20240325-1403, environment: Java 17.0.10 (Eclipse Adoptium)"
)
public class RequestMapperImpl implements RequestMapper {

    @Override
    public Request toEntity(RequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        RequestBuilder request = Request.builder();

        request.receiverName( requestDto.getReceiverName() );
        request.receiverNumber( requestDto.getReceiverNumber() );
        request.receiverSsn( requestDto.getReceiverSsn() );
        request.senderId( requestDto.getSenderId() );

        return request.build();
    }

    @Override
    public RequestDto toDto(Request request) {
        if ( request == null ) {
            return null;
        }

        RequestDto requestDto = new RequestDto();

        requestDto.setReceiverName( request.getReceiverName() );
        requestDto.setReceiverNumber( request.getReceiverNumber() );
        requestDto.setReceiverSsn( request.getReceiverSsn() );
        if ( request.getSenderId() != null ) {
            requestDto.setSenderId( request.getSenderId() );
        }

        return requestDto;
    }
}
