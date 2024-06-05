package com.example.ArticlesServices.external;

import com.example.Common.model.AuthorDTO;

public interface AuthorApiService {

    AuthorDTO getAuthorById(int authorId);
}
