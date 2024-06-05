package com.example.AuthorServices.controller;

import com.example.AuthorServices.dtos.AuthorRequest;
import com.example.AuthorServices.service.AuthorService;
import com.example.Common.Constants;
import com.example.Common.model.ArticleDTO;
import com.example.Common.model.AuthorDTO;
import com.example.Common.model.UserDetailsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<AuthorDTO>> getAllAuthors(@RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorService.getAllAuthors(jwt));
    }

    @GetMapping(value = "/following/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<AuthorDTO>> getAuthorsFollowing(@PathVariable int authorId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorService.getFollowing(authorId));
    }

    @GetMapping(value = "/followers/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<AuthorDTO>> getAuthorsFollowed(@PathVariable int authorId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorService.getFollowers(authorId));
    }

    @GetMapping(value = "/id/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable int authorId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorService.getAuthorById(authorId));
    }

    @GetMapping(value = "/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthorDTO> getAuthorByUsername(@PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorService.getAuthorByUsername(username));
    }


    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAuthor(@RequestBody AuthorRequest request) {
        authorService.saveAuthor(request);
    }

    @PutMapping(value = "/follow/{followingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void followAuthor(@RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt, @PathVariable int followingId) {
        authorService.followAuthor(followingId, jwt);
    }

    @PutMapping(value = "/unfollow/{followingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void unfollowAuthor(@RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt, @PathVariable int followingId) {
        authorService.unfollowAuthor(followingId, jwt);
    }

    @DeleteMapping(value = "/{authorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteAuthorById(@PathVariable int authorId) {
        authorService.deleteAuthorById(authorId);
    }
}
