package com.example.Common.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ArticleClientFallback implements ArticleClient {

    @Override
    public ResponseEntity<String> deleteArticlesByAuthorId(int authorId) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<String> getArticleById(int articleId) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<String> getArticlesByAuthor(int authorId) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
