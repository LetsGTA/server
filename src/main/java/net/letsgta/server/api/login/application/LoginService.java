package net.letsgta.server.api.login.application;

import net.letsgta.server.api.login.dto.request.LoginRequest;
import net.letsgta.server.api.login.dto.response.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest request);
}
