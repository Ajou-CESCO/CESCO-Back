package com.cesco.pillintime.mapper;

import com.cesco.pillintime.dto.RequestDto;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.entity.Request.RequestBuilder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-30T22:49:39+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.7.jar, environment: Java 18.0.2 (Azul Systems, Inc.)"
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

        return requestDto;
    }
}
