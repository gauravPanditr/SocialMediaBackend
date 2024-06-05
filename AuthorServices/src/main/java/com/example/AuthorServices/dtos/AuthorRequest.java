package com.example.AuthorServices.dtos;

import lombok.Data;

@Data
public class AuthorRequest {
    private final String firstName;
    private final String lastName;
    private final String username;

}
