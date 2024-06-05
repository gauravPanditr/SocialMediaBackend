package com.example.ArticlesServices.enities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"likes", "articles", "motherArticle"})
@Entity
@Table(name = "ARTICLES")
public class Articles {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ARTICLE_ID")
    private int id;

    @Column(name = "CREATE_AT")
    private Timestamp createAt;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "AUTHOR_ID")
    private int authorId;

    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "motherArticle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Articles> articles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOTHER_ARTICLE_ID")
    private Articles motherArticle;

    public Articles(int id, Timestamp createAt, String content, int authorId, Articles motherArticle) {
        this.id = id;
        this.createAt = createAt;
        this.content = content;
        this.authorId = authorId;
        this.motherArticle = motherArticle;
        this.likes = new ArrayList<>();
        this.articles = new ArrayList<>();
    }
}
