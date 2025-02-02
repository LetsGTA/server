package net.letsgta.server.api.login.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginException extends RuntimeException {

    private final LoginExceptionResult loginExceptionResult;
}
