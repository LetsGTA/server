package net.letsgta.server.config.exception.comment;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.letsgta.server.api.comment.exception.CommentException;
import net.letsgta.server.config.exception.common.ApiExceptionEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CommentApiExceptionAdvice {

    @ExceptionHandler({CommentException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest req, final CommentException e) {
        log.warn("[CommentApiExceptionAdvice] CommentException :: {}", e.getCommentExceptionResult().getMessage());

        return ResponseEntity
                .status(e.getCommentExceptionResult().getStatus())
                .body(ApiExceptionEntity.builder()
                        .errorCode(e.getCommentExceptionResult().getCode())
                        .errorMsg(e.getCommentExceptionResult().getMessage())
                        .build());
    }
}
