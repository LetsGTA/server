package net.letsgta.server.config.security;

import lombok.RequiredArgsConstructor;
import net.letsgta.server.api.oauth.application.CustomOAuth2UserService;
import net.letsgta.server.api.user.application.UserGetService;
import net.letsgta.server.api.user.enums.RoleName;
import net.letsgta.server.config.oauth.handler.OAuth2SuccessHandler;
import net.letsgta.server.config.security.filter.JwtAuthFilter;
import net.letsgta.server.config.security.handler.CustomAccessDeniedHandler;
import net.letsgta.server.config.security.handler.CustomAuthenticationEntryPointHandler;
import net.letsgta.server.config.security.provider.CustomAuthenticationProvider;
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

    private static final String POST_BASE = "/api/v1/post/**";
    private static final String CATEGORY_BASE = "/api/v1/category/**";
    private static final String USER_BASE = "/api/v1/user";

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
                // 공개 엔드포인트
                .requestMatchers(
                        "/api/v1/login/**",
                        "/api/v1/signup/**",
                        "/error",
                        "/favicon.ico"
                ).permitAll()

                // 게시글 관련 엔드포인트
                .requestMatchers(HttpMethod.GET, POST_BASE).permitAll()
                .requestMatchers(HttpMethod.POST, POST_BASE).authenticated()
                .requestMatchers(HttpMethod.PUT, POST_BASE).authenticated()
                .requestMatchers(HttpMethod.DELETE, POST_BASE).authenticated()

                // 카테고리 관련 엔드포인트
                .requestMatchers(HttpMethod.GET, CATEGORY_BASE).permitAll()
                .requestMatchers(HttpMethod.POST, CATEGORY_BASE).hasRole(RoleName.ROLE_ADMIN.getRole())
                .requestMatchers(HttpMethod.DELETE, CATEGORY_BASE).hasRole(RoleName.ROLE_ADMIN.getRole())

                // 사용자 관리 엔드포인트
                .requestMatchers(HttpMethod.DELETE, USER_BASE).hasRole(RoleName.ROLE_ADMIN.getRole())

                // 기타 요청
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
