package net.letsgta.server.api.user.application.impl;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.email.application.EmailVerificationService;
import net.letsgta.server.api.oauth.dto.response.OAuthUserResponse;
import net.letsgta.server.api.user.application.UserSignUpService;
import net.letsgta.server.api.user.dto.request.UserSignUpRequest;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.api.user.repository.UserRepository;
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
