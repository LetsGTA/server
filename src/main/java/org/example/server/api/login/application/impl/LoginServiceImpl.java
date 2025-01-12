package org.example.server.api.login.application.impl;

import lombok.RequiredArgsConstructor;
import org.example.server.api.login.application.LoginService;
import org.example.server.api.login.dto.request.LoginRequest;
import org.example.server.api.login.dto.response.LoginResponse;
import org.example.server.api.login.exception.LoginException;
import org.example.server.api.login.exception.LoginExceptionResult;
import org.example.server.api.token.repository.RefreshTokenRepository;
import org.example.server.api.user.application.UserGetService;
import org.example.server.api.user.dto.response.UserGetResponse;
import org.example.server.config.security.provider.JwtProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserGetService userGetService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 사용자 정보 조회
        UserGetResponse userInfo = userGetService.getUserByEmail(request.email());

        // password 일치 여부 체크
        if(!bCryptPasswordEncoder.matches(request.password(), userInfo.password())) {
            throw new LoginException(LoginExceptionResult.NOT_CORRECT);
        }

        // jwt 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(userInfo.userId());
        
        // refresh token 생성 후 저장
        String refreshToken = jwtProvider.generateRefreshToken(userInfo.userId());
        refreshTokenRepository.saveRefreshToken(refreshToken, userInfo.userId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
