package org.example.server.api.user.application.impl;

import lombok.RequiredArgsConstructor;
import org.example.server.api.email.application.EmailVerificationService;
import org.example.server.api.oauth.dto.response.OAuthUserResponse;
import org.example.server.api.user.application.UserSignUpService;
import org.example.server.api.user.dto.request.UserSignUpRequest;
import org.example.server.api.user.entity.User;
import org.example.server.api.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSignUpServiceImpl implements UserSignUpService {

    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    @Override
    public void signUp(UserSignUpRequest request) {
        // 최종 email 인증
        emailVerificationService.confirmEmail(request.token());

        User user = request.to();

        user.encryptPassword(bCryptPasswordEncoder);

        userRepository.save(user);

        // email 인증 정보 제거
        emailVerificationService.completeEmailVerification(request.token());
    }

    @Override
    public User signUp(OAuthUserResponse oAuthUserResponse) {
        User user = userRepository.findByEmail(oAuthUserResponse.email())
                .orElseGet(oAuthUserResponse::toEntity);

        return userRepository.save(user);
    }
}
