package org.example.server.api.common.response.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ResponseEnum {

    OK(HttpStatus.OK, "success", "성공"),
    FAIL(HttpStatus.OK, "fail", "실패");

    private final HttpStatus status;
    private final String result;
    private final String message;
}
