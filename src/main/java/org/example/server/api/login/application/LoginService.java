package org.example.server.api.login.application;

import org.example.server.api.login.dto.request.LoginRequest;
import org.example.server.api.login.dto.response.LoginResponse;

public interface LoginService {

    LoginResponse login(LoginRequest request);
}
