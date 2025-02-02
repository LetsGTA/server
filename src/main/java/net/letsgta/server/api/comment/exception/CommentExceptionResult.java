package net.letsgta.server.api.comment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentExceptionResult {

    NOT_EXISTS(HttpStatus.BAD_REQUEST, "cm0001", "존재하지 않는 댓글입니다."),
    INVALID_ACCESS(HttpStatus.FORBIDDEN, "cm0002", "댓글에 대한 권한이 없습니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "cm0003", "부모 댓글을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
