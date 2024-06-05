package com.example.Common.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "articles", url = "http://articles:2111/api")
public interface ArticleClient {

    @DeleteMapping("/authorId/{authorId}")
    ResponseEntity<String> deleteArticlesByAuthorId(@PathVariable("authorId") int authorId);

    @GetMapping("/id/{articleId}")
    ResponseEntity<String> getArticleById(@PathVariable("articleId") int articleId);

    @GetMapping("/author/{authorId}")
    ResponseEntity<String> getArticlesByAuthor(@PathVariable("authorId") int authorId);
}
