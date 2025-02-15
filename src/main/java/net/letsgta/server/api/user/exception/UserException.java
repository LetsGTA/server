package net.letsgta.server.api.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {

    private final UserExceptionResult userExceptionResult;
}
