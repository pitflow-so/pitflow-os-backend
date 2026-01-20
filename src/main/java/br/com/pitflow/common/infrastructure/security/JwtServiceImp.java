package br.com.pitflow.common.infrastructure.security;

import br.com.pitflow.registry.domain.Mechanic;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class JwtServiceImp implements JwtService {

    private final String secret;
    private final Integer expirationHours;

    public JwtServiceImp(String secret, Integer expirationHours) {
        this.secret = secret;
        this.expirationHours = expirationHours;
    }

    @Override
    public String generateToken(Mechanic mechanic) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            return Jwts.builder()
                    .subject(mechanic.getUsername())
                    .claim("name", mechanic.getName())
                    .claim("role", mechanic.getRole())
                    .issuedAt(new Date())
                    .expiration(generateExpirationDate())
                    .signWith(key)
                    .compact();
        } catch (Exception exception) {
            throw new RuntimeException("Error generating JWT token", exception);
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException exception) {
            return null;
        }
    }

    private Date generateExpirationDate() {
        return Date.from(LocalDateTime.now()
                .plusHours(expirationHours)
                .toInstant(ZoneOffset.of("-03:00")));
    }
}