package com.cesco.pillintime.api.request.mapper;

import com.cesco.pillintime.api.member.entity.Member;
import com.cesco.pillintime.api.request.dto.RequestDto;
import com.cesco.pillintime.api.request.entity.Request;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-09T19:19:26+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class RequestMapperImpl implements RequestMapper {

    @Override
    public RequestDto toDto(Request request) {
        if ( request == null ) {
            return null;
        }

        RequestDto requestDto = new RequestDto();

        requestDto.setSenderId( requestSenderId( request ) );
        requestDto.setSenderName( requestSenderName( request ) );
        requestDto.setSenderPhone( requestSenderPhone( request ) );
        requestDto.setId( request.getId() );
        requestDto.setReceiverPhone( request.getReceiverPhone() );

        return requestDto;
    }

    private Long requestSenderId(Request request) {
        if ( request == null ) {
            return null;
        }
        Member sender = request.getSender();
        if ( sender == null ) {
            return null;
        }
        Long id = sender.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String requestSenderName(Request request) {
        if ( request == null ) {
            return null;
        }
        Member sender = request.getSender();
        if ( sender == null ) {
            return null;
        }
        String name = sender.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String requestSenderPhone(Request request) {
        if ( request == null ) {
            return null;
        }
        Member sender = request.getSender();
        if ( sender == null ) {
            return null;
        }
        String phone = sender.getPhone();
        if ( phone == null ) {
            return null;
        }
        return phone;
    }
}
