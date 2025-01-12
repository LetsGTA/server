package org.example.server.api.user.dto.response;

import lombok.Builder;
import org.example.server.api.user.enums.RoleName;
import org.example.server.api.user.entity.User;

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
