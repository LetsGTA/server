package org.example.server.api.oauth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OAuthExceptionResult {

    // TODO : 구체적으로 변경
    NOT_CORRECT(HttpStatus.BAD_REQUEST, "l0001", "아이디 혹은 비밀번호가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
