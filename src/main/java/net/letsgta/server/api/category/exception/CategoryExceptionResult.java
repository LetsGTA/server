package net.letsgta.server.api.category.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CategoryExceptionResult {

    NOT_EXISTS(HttpStatus.BAD_REQUEST, "c0001", "존재하지 않는 카테고리입니다."),
    ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "c0002", "이미 존재하는 카테고리입니다."),
    NULL_POINTER(HttpStatus.INTERNAL_SERVER_ERROR, "c0003", "널 포인터 예외가 발생했습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "c0004", "잘못된 파라미터 값입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
