package net.letsgta.server.config.exception.category;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.letsgta.server.api.category.exception.CategoryException;
import net.letsgta.server.config.exception.common.ApiExceptionEntity;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CategoryApiExceptionAdvice {

    @ExceptionHandler({CategoryException.class})
    public ResponseEntity<ApiExceptionEntity> exceptionHandler(HttpServletRequest req, final CategoryException e) {
        log.warn("[CategoryApiExceptionAdvice] CategoryException :: {}", e.getCategoryExceptionResult().getMessage());

        return ResponseEntity
                .status(e.getCategoryExceptionResult().getStatus())
                .body(ApiExceptionEntity.builder()
                        .errorCode(e.getCategoryExceptionResult().getCode())
                        .errorMsg(e.getCategoryExceptionResult().getMessage())
                        .build());
    }
}
