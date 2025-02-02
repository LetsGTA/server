package net.letsgta.server.api.common.response.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseKeyEnum {

    LIST("list"),
    ONE("one"),
    TOTAL("total");

    private final String key;
}
