package com.example.ArticlesServices.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticleCreateRequest {

    private String text;


    private Integer articleMotherId;

}
