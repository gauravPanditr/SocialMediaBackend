package com.example.ArticlesServices.dtos;

import lombok.*;


import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticleDetailsResponse {

    private int id;
    private String text;
    private Timestamp timestamp;
    private String elapsed;
    private String createDate;
    private AuthorResponse author;
    private LikeDetailsResponse likes;
    private List<ArticleResponse> comments;
}
