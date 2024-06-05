package com.example.ArticlesServices.enities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "LIKES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LIKE_ID")
    private int id;

    @Column(name = "CREATE_AT")
    private Timestamp createAt;

    @Column(name = "AUTHOR_ID")
    private int authorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ARTICLE_ID")
    @ToString.Exclude
    private Articles article;


}
