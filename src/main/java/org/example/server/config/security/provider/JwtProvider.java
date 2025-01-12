package org.example.server.config.security.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidity; // Access Token 유효시간 (ms)

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidity; // Refresh Token 유효시간 (ms)

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String getUsernameFromJwtToken(final String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        // token 유효성 검증
        if(Boolean.FALSE.equals(validateToken(token))) {
            return null;
        }

        final Claims claims = getAllClaimsFromToken(token);

        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(final String id) {
        return generateAccessToken(id, new HashMap<>());
    }

    public String generateAccessToken(final long id) {
        return generateAccessToken(String.valueOf(id), new HashMap<>());
    }

    public String generateAccessToken(final String id, final Map<String, Object> claims) {
        return doGenerateAccessToken(id, claims);
    }

    private String doGenerateAccessToken(final String id, final Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setId(id)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity)) // 30분
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(final String userId) {
        return doGenerateRefreshToken(userId);
    }

    public String generateRefreshToken(final long useId) {
        return doGenerateRefreshToken(String.valueOf(useId));
    }

    private String doGenerateRefreshToken(final String useId) {
        return Jwts.builder()
                .setId(useId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity)) // 24시간
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
