package com.cesco.pillintime.request.mapper;

import com.cesco.pillintime.member.entity.Member;
import com.cesco.pillintime.request.dto.RequestDto;
import com.cesco.pillintime.request.entity.Request;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-23T19:18:02+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.6 (Oracle Corporation)"
)
public class RequestMapperImpl implements RequestMapper {

    @Override
    public Request toEntity(RequestDto requestDto) {
        if ( requestDto == null ) {
            return null;
        }

        Request.RequestBuilder request = Request.builder();

        request.receiverPhone( requestDto.getReceiverPhone() );

        return request.build();
    }

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
