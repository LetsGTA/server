package org.example.server.api.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.server.api.common.response.entity.ApiResponseEntity;
import org.example.server.api.user.application.UserSignUpService;
import org.example.server.api.user.dto.request.UserSignUpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserSignUpController {

    private final UserSignUpService userSignUpService;

    @PostMapping("/api/v1/signup")
    public  ResponseEntity<ApiResponseEntity<Object>> signUp(@Valid @RequestBody UserSignUpRequest request) {
        userSignUpService.signUp(request);

        return ApiResponseEntity.successResponseEntity();
    }
}
