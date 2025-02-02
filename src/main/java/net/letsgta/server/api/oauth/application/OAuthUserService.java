package net.letsgta.server.api.oauth.application;

import net.letsgta.server.api.user.entity.User;

public interface OAuthUserService {

    void register(User user);
}
