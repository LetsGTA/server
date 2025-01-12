package org.example.server.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.example.server.api.user.application.UserGetService;
import org.example.server.api.user.dto.response.UserGetResponse;
import org.example.server.config.security.provider.JwtProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserGetService userGetService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String token = request.getHeader("Authorization");

        String userId = null;

        // Bearer token 검증 후 user name 조회
        if(token != null && !token.isEmpty()) {
            String jwtToken = token.substring(7);

            userId = jwtProvider.getUsernameFromJwtToken(jwtToken);
        }

        if(userId != null && !userId.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Spring Security Context Holder 인증 정보 set
            SecurityContextHolder.getContext().setAuthentication(getUserAuth(userId));
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getUserAuth(String userId) {
        UserGetResponse userInfo = userGetService.getUserByUserId(Long.parseLong(userId));

        return new UsernamePasswordAuthenticationToken(userInfo.userId(),
                userInfo.password(),
                Collections.singleton(new SimpleGrantedAuthority(userInfo.role().name()))
        );
    }
}
