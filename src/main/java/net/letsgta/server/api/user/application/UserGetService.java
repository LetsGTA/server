package net.letsgta.server.api.user.application;

import net.letsgta.server.api.user.dto.response.UserGetResponse;

public interface UserGetService {

    UserGetResponse getUserByUserId(long id);
    UserGetResponse getUserByEmail(String email);
    boolean isUserExist(long id);
    boolean isUserExist(String email);
}
