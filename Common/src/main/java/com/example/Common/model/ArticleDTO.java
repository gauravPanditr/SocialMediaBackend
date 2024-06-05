package com.example.Common.model;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticleDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String timestamp;
    private String text;
    private int authorId;
}
