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
    ALREADY_EXISTS_OWNER(HttpStatus.BAD_REQUEST, 40003, "Already exists owner"),
    MEDICINE_NAME_IS_EMPTY(HttpStatus.BAD_REQUEST, 40004, "Medicine name is empty"),
    INVALID_USERTYPE(HttpStatus.BAD_REQUEST, 40005, "Invalid usertype"),
    INVALID_PAYMENT_NAME(HttpStatus.BAD_REQUEST, 40006, "Invalid payment name"),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, 40007, "Invalid payment amount"),
    ALREADY_EXISTS_RELATION(HttpStatus.BAD_REQUEST, 40008, "Already exists relation"),

    // 401 Unauthorized
    TOKEN_IS_INVALID(HttpStatus.UNAUTHORIZED, 40101, "Token is invalid"),
    TOKEN_IS_EXPIRED(HttpStatus.UNAUTHORIZED, 40102, "Token is expired"),
    TOKEN_IS_EMPTY(HttpStatus.UNAUTHORIZED, 40103, "Token is empty"),
    TOKEN_IS_WRONG(HttpStatus.UNAUTHORIZED, 40104, "Token is wrong"),

    // 403 Forbidden
    INVALID_USER_ACCESS(HttpStatus.FORBIDDEN, 40301, "Invalid user access"),
    MANAGER_IS_NOT_SUBSCRIBER(HttpStatus.FORBIDDEN, 40302, "Manger is not subscriber"),

    // 404 Not Found
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 40401, "Not found user"),
    NOT_FOUND_REQUEST(HttpStatus.NOT_FOUND, 40402, "Not found request"),
    NOT_FOUND_RELATION(HttpStatus.NOT_FOUND, 40403, "Not found relation"),
    NOT_FOUND_CABINET(HttpStatus.NOT_FOUND, 40404, "Not found cabinet"),
    NOT_FOUND_HEALTH(HttpStatus.NOT_FOUND, 40405, "Not found health"),
    NOT_FOUND_MEDICINE(HttpStatus.NOT_FOUND, 40406, "Not found medicine"),
    NOT_FOUND_PLAN(HttpStatus.NOT_FOUND, 40407, "Not found plan"),
    NOT_FOUND_LOG(HttpStatus.NOT_FOUND, 40408, "Not found log"),
    NOT_FOUND_OWNER(HttpStatus.NOT_FOUND, 40409, "Not found owner"),
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, 40410, "Not found payment"),

    // 500 Internal Server
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "Internal server error"),
    EXTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50002, "External server error"),
    FCM_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50003, "Fcm server error");

    private final HttpStatus httpStatus;	// HttpStatus
    private final int code;				// ACCOUNT-001
    private final String message;			// 설명

}
