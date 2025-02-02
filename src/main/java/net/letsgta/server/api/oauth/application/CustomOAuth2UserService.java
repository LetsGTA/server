package net.letsgta.server.api.oauth.application;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.oauth.dto.response.OAuthUserResponse;
import net.letsgta.server.api.user.application.UserSignUpService;
import net.letsgta.server.api.user.entity.User;
import net.letsgta.server.config.oauth.PrincipalDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserSignUpService userSignUpService;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // 2. registrationId 가져오기 (third-party id)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 유저 정보 dto 생성
        OAuthUserResponse oAuthUserResponse = OAuthUserResponse.of(registrationId, oAuth2UserAttributes);

        // 5. 회원가입 및 로그인
        User user = userSignUpService.signUp(oAuthUserResponse);

        // 6. OAuth2User 로 반환
        return new PrincipalDetails(user, oAuth2UserAttributes, userNameAttributeName);
    }
}
