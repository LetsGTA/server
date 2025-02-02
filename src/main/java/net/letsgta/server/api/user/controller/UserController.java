package net.letsgta.server.api.user.controller;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.common.response.entity.ApiResponseEntity;
import net.letsgta.server.api.user.application.UserDelService;
import net.letsgta.server.api.user.application.UserGetService;
import net.letsgta.server.api.user.dto.response.UserGetResponse;
import net.letsgta.server.util.jwt.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserGetService userGetService;
    private final UserDelService userDelService;

    @GetMapping
    public ResponseEntity<ApiResponseEntity<UserGetResponse>> dashboard(Authentication authentication) {
        // 사용자 정보 조회
        UserGetResponse result = userGetService.getUserByUserId(JwtUtil.getLoginId(authentication));

        return ApiResponseEntity.successResponseEntity(result);
    }

    // TODO : 삭제 처리 다시 할 것
    @DeleteMapping
    public ResponseEntity<ApiResponseEntity<Object>> delete(Authentication authentication) {
        // 사용자 정보 삭제
        userDelService.deleteUserByUserId(JwtUtil.getLoginId(authentication));

        return ApiResponseEntity.successResponseEntity();
    }
}
