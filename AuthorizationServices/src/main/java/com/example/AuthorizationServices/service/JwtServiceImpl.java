package com.example.AuthorizationServices.service;

import com.example.AuthorizationServices.constant.SecurityConstants;
import com.example.AuthorizationServices.enities.User;
import com.example.AuthorizationServices.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String buildJwt(User user, long expirationDate) {
        logger.info("Building token for user: " + user.getUsername());
        try {
            return Jwts.builder()
                    .setIssuer("Social Media")
                    .setSubject("JWT Token")
                    .claim("username", user.getUsername())
                    .claim("role", user.getRole())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(expirationDate))
                    .signWith(createSecretKey())
                    .compact();
        } catch (Exception e) {
            logger.error("Error building refresh token", e);
            throw e;
        }
    }

    @Override
    public Claims parseJwtClaims(String jwt) {
        logger.info("Parsing JWT claims");
        try {
            String jwtWithoutBearer = jwt.replaceFirst("Bearer ", "");
            return Jwts.parser()
                    .setSigningKey(createSecretKey())
                    .build()
                    .parseClaimsJws(jwtWithoutBearer)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token", e);
            throw e;
        } catch (Exception e) {
            logger.error("Error parsing JWT claims", e);
            throw e;
        }
    }


    private SecretKey createSecretKey() {
        byte[] keyBytes = SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
