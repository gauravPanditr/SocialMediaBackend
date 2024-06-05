package com.example.ArticlesServices.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticleUpdateRequest {

    private String text;
    private int articleId;

}
