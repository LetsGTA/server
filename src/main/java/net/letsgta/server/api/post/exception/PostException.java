package net.letsgta.server.api.post.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostException extends RuntimeException {

    private final PostExceptionResult postExceptionResult;
}
