package com.example.Common.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationClientFallback implements AuthorizationClient {

    @Override
    public ResponseEntity<String> getUserDetails(String jwt) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
