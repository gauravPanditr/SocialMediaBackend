package com.example.AuthorServices.service;


import com.example.AuthorServices.dtos.AuthorRequest;
import com.example.Common.model.AuthorDTO;

import java.util.List;

public interface AuthorService {
    List<AuthorDTO> getAllAuthors(String jwt);
    List<AuthorDTO> getFollowing(int authorId);
    List<AuthorDTO> getFollowers(int authorId);
    AuthorDTO getAuthorById(int authorId);
    AuthorDTO getAuthorByUsername(String username);
    void saveAuthor(AuthorRequest request);
    void followAuthor(int followingId, String jwt);
    void unfollowAuthor(int followingId, String jwt);
    void deleteAuthorById(int authorId);
}
