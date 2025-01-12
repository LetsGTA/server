package org.example.server.config.security.provider;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.example.server.api.login.exception.LoginException;
import org.example.server.api.login.exception.LoginExceptionResult;
import org.example.server.api.user.application.UserGetService;
import org.example.server.api.user.dto.response.UserGetResponse;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserGetService userGetService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        // 사용자 정보 조회
        UserGetResponse userInfo = userGetService.getUserByEmail(username);

        if(!bCryptPasswordEncoder.matches(password, userInfo.password())) {
            throw new LoginException(LoginExceptionResult.NOT_CORRECT);
        }

        return new UsernamePasswordAuthenticationToken(
                userInfo.email(),
                userInfo.password(),
                Collections.singleton(new SimpleGrantedAuthority(userInfo.role().name()))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
