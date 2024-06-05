package com.example.AuthorizationServices.service;

import com.example.AuthorizationServices.enities.User;
import io.jsonwebtoken.Claims;

public interface JwtService {
    String buildJwt(User user, long expirationDate);
    Claims parseJwtClaims(String jwt);
}
