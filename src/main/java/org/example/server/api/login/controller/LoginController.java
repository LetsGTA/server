package org.example.server.api.login.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.api.common.response.entity.ApiResponseEntity;
import org.example.server.api.login.application.LoginService;
import org.example.server.api.login.dto.request.LoginRequest;
import org.example.server.api.login.dto.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/api/v1/login")
    public ResponseEntity<ApiResponseEntity<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        return ApiResponseEntity.successResponseEntity(loginService.login(request));
    }
}
