package net.letsgta.server.api.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleName {

    ROLE_ADMIN("ADMIN"),
    ROLE_USER("USER");

    private final String role;
}
