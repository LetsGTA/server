package org.example.server.api.oauth.repository;

import org.example.server.api.oauth.entity.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthUserRepository extends JpaRepository<OAuthUser, Long> {
}
