package net.letsgta.server.api.email.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmailExceptionResult {

    NOT_EXIST(HttpStatus.NOT_FOUND, "m0001", "인증 Email 이 존재하지 않습니다."),
    ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "m0002", "이미 이메일 인증이 완료된 상태입니다."),
    EXPIRED(HttpStatus.BAD_REQUEST, "m0003", "이메일 인증 시간이 초과되었습니다. 재시도해주세요."),
    TOO_MANY_ATTEMPTS(HttpStatus.BAD_REQUEST, "m0004", "너무 많은 시도를 하였습니다. 재시도해주세요."),
    INVALID_VERIFICATION_NUMBER(HttpStatus.BAD_REQUEST, "m0005", "인증번호가 올바르지 않습니다."),
    VERIFICATION_NOT_COMPLETE(HttpStatus.BAD_REQUEST, "m0006", "이메일 인증이 완료되지 않았습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
