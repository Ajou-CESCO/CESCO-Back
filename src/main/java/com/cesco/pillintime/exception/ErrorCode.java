package com.cesco.pillintime.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    ALREADY_EXISTS_PHONE(HttpStatus.BAD_REQUEST, 40001, "Already exists phone"),
    ALREADY_EXISTS_SSN(HttpStatus.BAD_REQUEST, 40002, "Already exists ssn"),

    // 401 Unauthorized
    TOKEN_IS_INVALID(HttpStatus.UNAUTHORIZED, 40101, "Token is invalid"),
    TOKEN_IS_EXPIRED(HttpStatus.UNAUTHORIZED, 40102, "Token is expired"),
    TOKEN_IS_EMPTY(HttpStatus.UNAUTHORIZED, 40103, "Token is empty"),
    TOKEN_IS_WRONG(HttpStatus.UNAUTHORIZED, 40104, "Token is wrong"),

    // 403 Forbidden
    INVALID_USER_ACCESS(HttpStatus.FORBIDDEN, 40301, "Invalid user access"),

    // 404 Not Found
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 40401, "Not found user"),
    NOT_FOUND_REQUEST(HttpStatus.NOT_FOUND, 40402, "Not found request"),
    NOT_FOUND_RELATION(HttpStatus.NOT_FOUND, 40403, "Not found relation"),
    NOT_FOUND_CABINET(HttpStatus.NOT_FOUND, 40404, "Not found cabinet"),
    NOT_FOUND_HEALTH(HttpStatus.NOT_FOUND, 40405, "Not found health"),
    NOT_FOUND_MEDICINE(HttpStatus.NOT_FOUND, 40406, "Not found medicine"),

    // 500 Internal Server
    EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "External api error");

    private final HttpStatus httpStatus;	// HttpStatus
    private final int code;				// ACCOUNT-001
    private final String message;			// 설명

}
