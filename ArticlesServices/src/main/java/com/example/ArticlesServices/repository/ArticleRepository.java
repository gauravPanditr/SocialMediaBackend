package com.example.ArticlesServices.repository;

import com.example.ArticlesServices.enities.Articles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Articles, Integer> {

    @Query("SELECT a FROM Articles a LEFT JOIN FETCH a.likes l WHERE l.authorId = :userId")
    List<Articles> findLikedArticlesByUserId(@Param("userId") int userId);



    @Query(
            value = "SELECT a FROM Articles a LEFT JOIN FETCH a.likes",
            countQuery = "SELECT COUNT(a) FROM Articles a"
    )
    Page<Articles> findAll(Pageable pageable);

    @Query(
            value = "SELECT a FROM Articles a LEFT JOIN FETCH a.likes"
    )
    List<Articles> findAll();

    int countAllByMotherArticleId(int motherArticleId);

    @Query(
            value = "SELECT a FROM Articles a LEFT JOIN FETCH a.likes WHERE a.id = :articleId"
    )
    Optional<Articles> findById(@Param("articleId") int articleId);

    @Query(
            value = "SELECT a FROM Articles a LEFT JOIN FETCH a.likes WHERE a.id = :articleId"
    )
    Optional<Articles> findByIdWithLikes(@Param("articleId") int articleId);

    @Query(
            value = "SELECT a FROM Articles a LEFT JOIN FETCH a.articles WHERE a.id = :articleId"
    )
    Optional<Articles> findByIdWithArticles(@Param("articleId") int articleId);

    @Query(
            value = "SELECT a FROM Articles a LEFT JOIN FETCH a.likes WHERE a.authorId = :authorId ORDER BY a.createAt DESC"
    )
    List<Articles> findAllByAuthorIdOrderByCreateAt(@Param("authorId") int authorId);

    @Query(
            value = "SELECT a FROM Articles a LEFT JOIN FETCH a.likes WHERE a.authorId IN :authorIds ORDER BY a.createAt DESC"
    )
    Page<Articles> findAllByAuthorIdInOrderByCreateAt(@Param("authorIds") List<Integer> authorIds, Pageable pageable);

    @Transactional
    void deleteAllByAuthorId(int authorId);
}
