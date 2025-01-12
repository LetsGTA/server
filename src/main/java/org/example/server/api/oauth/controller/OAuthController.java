package org.example.server.api.oauth.controller;

import lombok.RequiredArgsConstructor;
import org.example.server.api.common.response.entity.ApiResponseEntity;
import org.example.server.api.oauth.application.OAuthUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login/oauth/code")
public class OAuthController {

    private final OAuthUserService oAuthUserService;

    @GetMapping("/{registrationId}")
    public ResponseEntity<ApiResponseEntity<Object>> google(@RequestParam String code, @PathVariable String registrationId) {
        System.out.println(code);
        System.out.println(registrationId);
        return ApiResponseEntity.successResponseEntity();
    }
}
