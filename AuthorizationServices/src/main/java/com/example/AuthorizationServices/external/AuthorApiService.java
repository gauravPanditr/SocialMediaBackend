package com.example.AuthorizationServices.external;

import com.example.Common.model.AuthorDTO;
import com.example.Common.model.AuthorRequest;
import com.example.Common.model.UserDetailsDTO;

public interface AuthorApiService {
    AuthorDTO getAuthorByUsername(String username);
    void createAuthor(AuthorRequest authorRequest);
    void deleteAuthorById(int id);

}
