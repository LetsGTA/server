package net.letsgta.server.api.oauth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuthException extends RuntimeException {

    private final OAuthExceptionResult oAuthExceptionResult;
}
