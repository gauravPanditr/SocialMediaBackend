package com.example.AuthorServices.repository;

import com.example.AuthorServices.enities.Follow;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {

    @Transactional
    void deleteByFollowerIdAndFollowingId(Integer authorId, Integer followingId);
}
