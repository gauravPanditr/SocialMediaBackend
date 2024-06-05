package com.example.ArticlesServices.controller;

import com.example.ArticlesServices.dtos.*;
import com.example.ArticlesServices.services.ArticleService;
import com.example.Common.Constants;
import com.example.Common.model.ArticleDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController

public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // POST
    @PostMapping(value = "/articles",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ArticleResponse> saveArticle(
            @RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt,
            @RequestBody ArticleCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleService.saveArticle(request, jwt));
    }

    @PostMapping(value = "/{articleId}/like", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LikeActionResponse> likeArticle(
            @RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt,
            @PathVariable int articleId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleService.handleLikeAction(articleId, jwt));
    }

    @GetMapping("/articles/{userId}")
    public ResponseEntity<List<ArticleDTO>> getLikedArticlesForUser(@PathVariable int userId) {
        List<ArticleDTO> likedArticles = articleService.getLikedArticlesForUser(userId);
        return new ResponseEntity<>(likedArticles, HttpStatus.OK);
    }

    // PUT
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateArticle(
            @RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt,
            @RequestBody ArticleUpdateRequest request) {
        articleService.updateArticle(request, jwt);
    }

    // GET
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ArticleResponse>> getLatestArticles(
            @RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.getLatestArticles(page, size, jwt));
    }

    @GetMapping(value = "/authors/followed", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ArticleResponse>> getLatestFollowingArticles(
            @RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.getLatestFollowingArticles(page, size, jwt));
    }

    @GetMapping(value = "/{articleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ArticleDetailsResponse> getArticleDetailsById(
            @RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt,
            @PathVariable int articleId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.getArticle(articleId, jwt));
    }

    @GetMapping(value = "/author/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ArticleDTO>> getArticlesByAuthorById(
            @PathVariable int authorId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.getArticlesByAuthorId(authorId));
    }

    @GetMapping(value = "/id/{articleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ArticleDTO> getArticleById(
            @PathVariable int articleId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(articleService.getArticleById(articleId));
    }

    // DELETE
    @DeleteMapping(value = "/{articleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticleById(
            @RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt,
            @PathVariable int articleId) {
        articleService.deleteArticleById(articleId, jwt);
    }

    @DeleteMapping(value = "/authorId/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticlesByAuthorId(
            @PathVariable int authorId) {
        articleService.deleteArticlesByAuthorId(authorId);
    }
}
