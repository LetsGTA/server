package org.example.server.api.user.application;

import org.example.server.api.oauth.dto.response.OAuthUserResponse;
import org.example.server.api.user.dto.request.UserSignUpRequest;
import org.example.server.api.user.entity.User;

public interface UserSignUpService {

    void signUp(UserSignUpRequest request);
    User signUp(OAuthUserResponse oAuthUserResponse);
}
