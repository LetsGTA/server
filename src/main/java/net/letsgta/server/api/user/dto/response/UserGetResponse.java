package net.letsgta.server.api.user.dto.response;

import lombok.Builder;
import net.letsgta.server.api.user.enums.RoleName;
import net.letsgta.server.api.user.entity.User;

@Builder
public record UserGetResponse(

        long userId,
        String email,
        String password,
        String nickname,
        RoleName role

) {
        public static UserGetResponse from(User user) {
                return UserGetResponse.builder()
                        .userId(user.getUserId())
                        .password(user.getPassword())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .role(user.getRole())
                        .build();
        }
}
