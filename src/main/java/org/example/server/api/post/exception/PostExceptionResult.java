package org.example.server.api.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostExceptionResult {

    NOT_EXISTS(HttpStatus.BAD_REQUEST, "p0001", "존재하지 않는 게시글입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
