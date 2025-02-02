package net.letsgta.server.api.oauth.application.impl;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.oauth.application.OAuthUserService;
import net.letsgta.server.api.oauth.entity.OAuthUser;
import net.letsgta.server.api.oauth.repository.OauthUserRepository;
import net.letsgta.server.api.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthUserUserServiceImpl implements OAuthUserService {

    private final OauthUserRepository oauthUserRepository;

    @Override
    public void register(User user) {
        OAuthUser oauthUser = OAuthUser.builder()
                .user(user)
                .provider("google")
                .providerId("1234")
                .build();

        oauthUserRepository.save(oauthUser);
    }
}
