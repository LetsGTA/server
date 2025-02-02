package net.letsgta.server.api.oauth.repository;

import net.letsgta.server.api.oauth.entity.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthUserRepository extends JpaRepository<OAuthUser, Long> {
}
