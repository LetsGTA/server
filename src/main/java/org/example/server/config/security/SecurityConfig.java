package org.example.server.config.security;

import lombok.RequiredArgsConstructor;
import org.example.server.api.oauth.application.CustomOAuth2UserService;
import org.example.server.api.user.application.UserGetService;
import org.example.server.api.user.enums.RoleName;
import org.example.server.config.oauth.handler.OAuth2SuccessHandler;
import org.example.server.config.security.filter.JwtAuthFilter;
import org.example.server.config.security.handler.CustomAccessDeniedHandler;
import org.example.server.config.security.handler.CustomAuthenticationEntryPointHandler;
import org.example.server.config.security.provider.CustomAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserGetService userGetService;
    private final CustomAuthenticationEntryPointHandler customAuthenticationEntryPointHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // http request 인증 설정
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/api/v1/login/**",
                        "/api/v1/signup/**",
                        "/error",
                        "/favicon.ico"
                ).permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/user").hasRole(RoleName.ROLE_ADMIN.getRole())
                .anyRequest().authenticated()
        );

        // basic
        http.httpBasic(AbstractHttpConfigurer::disable);

        // form login
        http.formLogin(AbstractHttpConfigurer::disable);

        // logout
        http.logout(AbstractHttpConfigurer::disable);

        // csrf
        http.csrf(AbstractHttpConfigurer::disable);

        // cors
        http.cors(AbstractHttpConfigurer::disable);

        // session management
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // jwt filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // entry point handler
        http.exceptionHandling(conf -> conf
                .authenticationEntryPoint(customAuthenticationEntryPointHandler)
                .accessDeniedHandler(customAccessDeniedHandler)
        );

        // oauth2
        http.oauth2Login(oauth -> oauth
                .userInfoEndpoint(c -> c.userService(oAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
        );

        return http.build();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(bCryptPasswordEncoder, userGetService);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        CustomAuthenticationProvider authProvider = customAuthenticationProvider();
        return new ProviderManager(authProvider);
    }
}
