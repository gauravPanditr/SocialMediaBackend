package com.example.ArticlesServices.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class AuthorResponse {

    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private boolean isFollowed;

}
