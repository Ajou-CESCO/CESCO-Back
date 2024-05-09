package com.cesco.pillintime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400
    ALREADY_EXISTS_PHONE(HttpStatus.BAD_REQUEST, 40001, "Already exists phone"),

    // 403
    NOT_FORBIDDEN_USER(HttpStatus.FORBIDDEN, 40401, "not forbidden user"),

    // 404
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 40401, "Not found user"),
    NOT_FOUND_REQUEST(HttpStatus.NOT_FOUND, 40402, "Not found request"),
    NOT_FOUND_RELATION(HttpStatus.NOT_FOUND, 40403, "Not found relation");

    private final HttpStatus httpStatus;	// HttpStatus
    private final int code;				// ACCOUNT-001
    private final String message;			// 설명

}
