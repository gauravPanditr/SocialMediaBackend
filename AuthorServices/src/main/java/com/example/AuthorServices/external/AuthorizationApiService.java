package com.example.AuthorServices.external;


import com.example.Common.model.UserDetailsDTO;

public interface AuthorizationApiService {
    UserDetailsDTO getUserDetailsAndValidate(String jwt, String... roles);
}
