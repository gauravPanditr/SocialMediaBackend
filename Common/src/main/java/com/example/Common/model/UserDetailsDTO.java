package com.example.Common.model;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter

public class UserDetailsDTO {
    private final int authorId;
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String role;
}
