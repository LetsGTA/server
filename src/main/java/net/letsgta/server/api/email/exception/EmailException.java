package net.letsgta.server.api.email.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailException extends RuntimeException {

    private final EmailExceptionResult emailExceptionResult;
}
