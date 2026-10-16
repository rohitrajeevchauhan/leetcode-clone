package com.rohit.leetcode.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMs
    ) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException(
                    "JWT secret must not be empty"
            );
        }

        /*
         * HS256 requires a sufficiently long secret.
         */
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 bytes long"
            );
        }

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expirationMs = expirationMs;
    }

    // =========================================================
    // GENERATE TOKEN
    // =========================================================

    public String generateToken(String email) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + expirationMs
        );

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    // =========================================================
    // EXTRACT EMAIL / USERNAME
    // =========================================================

    public String extractUsername(String token) {

        Claims claims = extractAllClaims(token);

        return claims.getSubject();
    }

    // =========================================================
    // EXTRACT CLAIMS
    // =========================================================

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // =========================================================
    // VALIDATE TOKEN
    // =========================================================

    public boolean isTokenValid(String token) {

        try {

            Claims claims = extractAllClaims(token);

            String username = claims.getSubject();

            Date expiration = claims.getExpiration();

            return username != null
                    && !username.isBlank()
                    && expiration != null
                    && expiration.after(new Date());

        } catch (Exception e) {

            return false;
        }
    }

    // =========================================================
    // EXTRACT TOKEN FROM HEADER
    // =========================================================

    public String extractTokenFromHeader(String authHeader) {

        if (authHeader == null
                || authHeader.isBlank()
                || !authHeader.startsWith("Bearer ")) {

            return null;
        }

        String token = authHeader.substring(7).trim();

        return token.isBlank() ? null : token;
    }

    // =========================================================
    // GET TOKEN EXPIRATION
    // =========================================================

    public Date getExpiration(String token) {

        return extractAllClaims(token).getExpiration();
    }

    // =========================================================
    // GET REMAINING TOKEN TIME
    // =========================================================

    public long getRemainingExpirationMs(String token) {

        Date expiration = getExpiration(token);

        return Math.max(
                0,
                expiration.getTime() - System.currentTimeMillis()
        );
    }
}