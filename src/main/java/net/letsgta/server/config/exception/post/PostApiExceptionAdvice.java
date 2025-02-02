package net.letsgta.server.config.exception.post;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.letsgta.server.api.post.exception.PostException;
import net.letsgta.server.config.exception.common.ApiExceptionEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class PostApiExceptionAdvice {

    @ExceptionHandler({PostException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest req, final PostException e) {
        log.warn("[PostApiExceptionAdvice] PostException :: {}", e.getPostExceptionResult().getMessage());

        return ResponseEntity
                .status(e.getPostExceptionResult().getStatus())
                .body(ApiExceptionEntity.builder()
                        .errorCode(e.getPostExceptionResult().getCode())
                        .errorMsg(e.getPostExceptionResult().getMessage())
                        .build());
    }
}
