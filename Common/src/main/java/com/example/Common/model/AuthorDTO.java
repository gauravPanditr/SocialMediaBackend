package com.example.Common.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class AuthorDTO {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final List<Integer> following;
    private final List<Integer> followers;
}
