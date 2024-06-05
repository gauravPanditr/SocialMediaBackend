package com.example.AuthorServices.repository;


import com.example.AuthorServices.enities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    @Override
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.followedBy")
    List<Author> findAll();

    @Override
    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.followedBy WHERE a.id = :authorId")
    Optional<Author> findById(@Param("authorId") Integer authorId);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.followedBy WHERE a.username = :username")
    Optional<Author> findAuthorByUsername(@Param("username") String username);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.followedBy f WHERE f.follower.id = :theId")
    List<Author> findAllFollowing(@Param("theId") Integer theId);

    @Query("SELECT a FROM Author a LEFT JOIN FETCH a.following f WHERE f.following.id = :theId")
    List<Author> findAllFollowers(@Param("theId") Integer theId);
}
