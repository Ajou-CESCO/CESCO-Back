package com.cesco.pillintime.mapper;

import com.cesco.pillintime.dto.RequestDto;
import com.cesco.pillintime.entity.Request;
import com.cesco.pillintime.entity.Request.RequestBuilder;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-25T16:37:56+0900",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.7.jar, environment: Java 22 (Oracle Corporation)"
)
public class RequestMapperImpl implements RequestMapper {

    @Override
    public Request toEntity(RequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        RequestBuilder request = Request.builder();

        request.senderId( requestDto.getSenderId() );
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

        if ( request.getSenderId() != null ) {
            requestDto.setSenderId( request.getSenderId() );
        }
        requestDto.setReceiverName( request.getReceiverName() );
        requestDto.setReceiverNumber( request.getReceiverNumber() );
        requestDto.setReceiverSsn( request.getReceiverSsn() );

        return requestDto;
    }
}
