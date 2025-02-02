package net.letsgta.server.config.exception.email;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.letsgta.server.api.email.exception.EmailException;
import net.letsgta.server.config.exception.common.ApiExceptionEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class EmailApiExceptionAdvice {

    @ExceptionHandler({EmailException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest req, final EmailException e) {
        log.warn("[EmailApiExceptionAdvice] EmailException :: {}", e.getEmailExceptionResult().getMessage());

        return ResponseEntity
                .status(e.getEmailExceptionResult().getStatus())
                .body(ApiExceptionEntity.builder()
                        .errorCode(e.getEmailExceptionResult().getCode())
                        .errorMsg(e.getEmailExceptionResult().getMessage())
                        .build());
    }
}
