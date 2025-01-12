package org.example.server.api.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserExceptionResult {

    NOT_EXISTS(HttpStatus.BAD_REQUEST, "u0001", "존재하지 않는 사용자입니다."),
    ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "u0002", "이미 존재하는 이메일입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
