package org.example.server.api.token.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.api.common.response.entity.ApiResponseEntity;
import org.example.server.api.token.application.RefreshTokenService;
import org.example.server.api.token.dto.request.RefreshTokenRequest;
import org.example.server.api.token.dto.response.RefreshTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/api/v1/login/token-refresh")
    public ResponseEntity<ApiResponseEntity<RefreshTokenResponse>> tokenRefresh(@Valid @RequestBody RefreshTokenRequest request) {
        // token 재발급
        var result = refreshTokenService.refreshToken(request.refreshToken());

        return ApiResponseEntity.successResponseEntity(result);
    }
}
