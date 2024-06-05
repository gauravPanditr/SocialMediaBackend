package com.example.Common.client;


import com.example.Common.model.AuthorRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class AuthorClientFallback implements AuthorClient {

    @Override
    public ResponseEntity<String> getAuthorById(int authorId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> createAuthor(AuthorRequest theAuthor) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<Void> deleteAuthorById(int authorId) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<String> getAuthorByUsername(String username) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<String> deleteArticlesByAuthorId(int authorId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
