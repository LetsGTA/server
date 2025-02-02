package net.letsgta.server.api.comment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentException extends RuntimeException{

    private final CommentExceptionResult commentExceptionResult;
}
