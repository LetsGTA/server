package org.example.server.api.oauth.application;

import org.example.server.api.user.entity.User;

public interface OAuthUserService {

    void register(User user);
}
