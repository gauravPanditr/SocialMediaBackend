package com.example.AuthorizationServices.dtos;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private int following;
    private int followers;

    private String token;
    private String refreshToken;

    private String role;
    private long tokenExpirationDate;
    private long refreshTokenExpirationDate;


}
