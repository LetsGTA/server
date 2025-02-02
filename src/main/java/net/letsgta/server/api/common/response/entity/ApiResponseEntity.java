package net.letsgta.server.api.common.response.entity;

import java.util.Objects;
import lombok.Builder;
import net.letsgta.server.api.common.response.enums.ResponseEnum;
import org.springframework.http.ResponseEntity;

public record ApiResponseEntity<T>(
        String result,
        String msg,
        T data
)
{

    @Builder
    public ApiResponseEntity(String result, String msg, T data) {
        this.result = (result == null || result.isEmpty()) ? ResponseEnum.OK.getResult() : result;
        this.msg = (msg == null || msg.isEmpty()) ? ResponseEnum.OK.getMessage() : msg;
        this.data = data;
    }

    public static <T> ResponseEntity<ApiResponseEntity<T>> successResponseEntity() {
        return ResponseEntity.ok(
                ApiResponseEntity.<T>builder()
                        .msg("요청 성공")
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponseEntity<T>> successResponseEntity(T data) {
        return ResponseEntity.ok(
                ApiResponseEntity.<T>builder()
                        .data(data)
                        .msg("요청 성공")
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponseEntity<T>> failResponseEntity(String msg) {
        return ResponseEntity.ok(
                ApiResponseEntity.<T>builder()
                        .result(ResponseEnum.FAIL.getResult())
                        .msg(!Objects.equals(msg, "요청 실패") ? msg : ResponseEnum.FAIL.getMessage())
                        .build()
        );
    }

    public static <T> ResponseEntity<ApiResponseEntity<T>> failResponseEntity(T data, String msg) {
        return ResponseEntity.ok(
                ApiResponseEntity.<T>builder()
                        .result(ResponseEnum.FAIL.getResult())
                        .msg(!Objects.equals(msg, "요청 실패") ? msg : ResponseEnum.FAIL.getMessage())
                        .data(data)
                        .build()
        );
    }
}
