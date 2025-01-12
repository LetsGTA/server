package org.example.server.api.oauth.dto.response;

import java.util.Map;
import lombok.Builder;
import org.example.server.api.oauth.exception.OAuthException;
import org.example.server.api.oauth.exception.OAuthExceptionResult;
import org.example.server.api.user.entity.User;
import org.example.server.api.user.enums.RoleName;

@Builder
public record OAuthUserResponse(
        String name,
        String email
) {
    public static OAuthUserResponse of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) { // registration id 별로 userInfo 생성
            case "google" -> ofGoogle(attributes);
            case "kakao" -> ofKakao(attributes);
            default -> throw new OAuthException(OAuthExceptionResult.NOT_CORRECT);
        };
    }

    private static OAuthUserResponse ofGoogle(Map<String, Object> attributes) {
        return OAuthUserResponse.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private static OAuthUserResponse ofKakao(Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuthUserResponse.builder()
                .name((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .build();
    }

    public User toEntity() {
        return User.builder()
                .nickname(name)
                .email(email)
                .role(RoleName.ROLE_USER)
                .build();
    }
}
