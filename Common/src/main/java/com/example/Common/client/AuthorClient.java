package com.example.Common.client;


import com.example.Common.model.AuthorRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "AuthorServices", url = "http://localhost:2112/api", fallback = AuthorClientFallback.class)
public interface AuthorClient {

    @GetMapping("/id/{authorId}")
    ResponseEntity<String> getAuthorById(@PathVariable("authorId") int authorId);

    @PostMapping("/")
    ResponseEntity<Void> createAuthor(@RequestBody AuthorRequest theAuthor);

    @DeleteMapping("/{authorId}")
    ResponseEntity<Void> deleteAuthorById(@PathVariable("authorId") int authorId);

    @GetMapping("/username/{username}")
    ResponseEntity<String> getAuthorByUsername(@PathVariable("username") String username);

    @DeleteMapping("/authorId/{authorId}")
    ResponseEntity<String> deleteArticlesByAuthorId(@PathVariable("authorId") int authorId);
}
