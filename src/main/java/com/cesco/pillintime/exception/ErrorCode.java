package com.cesco.pillintime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    ALREADY_EXISTS_PHONE(HttpStatus.BAD_REQUEST, 40001, "Already exists phone"),

    // 404 Not Found
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 40401, "Not found user"),
    NOT_FOUND_REQUEST(HttpStatus.NOT_FOUND, 40402, "Not found request"),
    NOT_FOUND_CABINET(HttpStatus.NOT_FOUND, 40403, "Not found cabinet"),

    // 500 Internal Server
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "External api error");


    private final HttpStatus httpStatus;	// HttpStatus
    private final int code;				// ACCOUNT-001
    private final String message;			// 설명

}
