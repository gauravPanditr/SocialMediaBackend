package com.example.AuthorizationServices.service;


import com.example.AuthorizationServices.dtos.LoginRequest;
import com.example.AuthorizationServices.dtos.LoginResponse;
import com.example.AuthorizationServices.dtos.RegisterRequest;
import com.example.Common.model.UserDetailsDTO;

public interface AuthorizationService {
    void registerUser(RegisterRequest request);
    void deleteUser(String jwt);
    LoginResponse loginUser(LoginRequest request);
    LoginResponse refreshAccessToken(String jwt);
    UserDetailsDTO getUserDetails(String jwt);
}
