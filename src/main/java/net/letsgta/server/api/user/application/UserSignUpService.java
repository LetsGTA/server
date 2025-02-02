package net.letsgta.server.api.user.application;

import net.letsgta.server.api.oauth.dto.response.OAuthUserResponse;
import net.letsgta.server.api.user.dto.request.UserSignUpRequest;
import net.letsgta.server.api.user.entity.User;

public interface UserSignUpService {

    void signUp(UserSignUpRequest request);
    User signUp(OAuthUserResponse oAuthUserResponse);
}
