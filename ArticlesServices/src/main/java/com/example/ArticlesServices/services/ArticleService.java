package com.example.ArticlesServices.services;

import com.example.ArticlesServices.dtos.*;
import com.example.ArticlesServices.enities.Articles;
import com.example.ArticlesServices.enities.Like;
import com.example.ArticlesServices.exception.ArticleNotFoundException;
import com.example.ArticlesServices.external.AuthorApiService;
import com.example.ArticlesServices.external.AuthorizationApiService;
import com.example.ArticlesServices.repository.ArticleRepository;
import com.example.Common.Constants;
import com.example.Common.exception.UnauthorizedException;
import com.example.Common.model.ArticleDTO;
import com.example.Common.model.AuthorDTO;
import com.example.Common.model.UserDetailsDTO;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class ArticleService {

    private final ArticleRepository articleRepository;
    private final AuthorApiService authorService;
    private final AuthorizationApiService authorizationService;
    private final ArticleResponseFactory articleResponseFactory;
  //  private final KafkaLikeService kafkaLikeService;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ArticleService.class);

    public ArticleService(ArticleRepository articleRepository, AuthorApiService authorService,
                          AuthorizationApiService authorizationService, ArticleResponseFactory articleResponseFactory
              ) {
        this.articleRepository = articleRepository;
        this.authorService = authorService;
        this.authorizationService = authorizationService;
        this.articleResponseFactory = articleResponseFactory;
     //   this.kafkaLikeService = kafkaLikeService;
    }

    public ArticleResponse saveArticle(ArticleCreateRequest request, String jwt) {
        logger.info("Saving article");
        UserDetailsDTO userDetails = authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_USER, Constants.ROLE_ADMIN);
        Articles motherArticle = request.getArticleMotherId() != null ?
                articleRepository.findById(request.getArticleMotherId())
                        .orElseThrow(() -> new ArticleNotFoundException("Article not found")) :
                null;
        Articles article = from(request, userDetails, motherArticle);

        try {
            Articles created = articleRepository.save(article);
            //sendArticleMessage(created);
            logger.info("Article saved successfully");
            return articleResponseFactory.createResponse(created, userDetails.getAuthorId());
        } catch (Exception e) {
            // Handle potential exceptions during save
            logger.error("Error saving article", e);
            throw new RuntimeException("An error occurred while saving the article"); // Consider a more specific exception type based on the expected errors
        }
    }


    public LikeActionResponse handleLikeAction(int articleId, String jwt) {
        logger.info("Processing like for article ID: " + articleId);
        UserDetailsDTO userDetails = authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_USER);
        Articles article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));

        boolean hasLiked = article.getLikes().stream().anyMatch(like -> like.getAuthorId() == userDetails.getAuthorId());
        if (hasLiked) {
            removeLike(article, userDetails.getAuthorId());
            return new LikeActionResponse(Constants.DISLIKE_ACTION);
        } else {
            addLike(article, userDetails.getAuthorId());
            return new LikeActionResponse(Constants.LIKE_ACTION);
        }
    }

    public void updateArticle(ArticleUpdateRequest request, String jwt) {
        logger.info("Updating article by ID: " + request.getArticleId());
        UserDetailsDTO userDetails = authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_USER, Constants.ROLE_ADMIN);
        Articles article = articleRepository.findById(request.getArticleId())
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));

        if (canUpdateArticle(article.getAuthorId(), userDetails.getAuthorId())) {
            Articles updated = from(request, article, userDetails);
            articleRepository.save(updated);
            logger.info("Article updated successfully");
        } else {
            handleUnauthorizedUpdateAttempt(userDetails.getUsername());
        }
    }

    public List<ArticleResponse> getLatestArticles(int page, int size, String jwt) {
        logger.info("Fetching latest articles, page: " + page + ", size: " + size);
        UserDetailsDTO userDetails = authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_USER, Constants.ROLE_ADMIN);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        return articleRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .map(article -> articleResponseFactory.createResponse(article, userDetails.getAuthorId()))
                .collect(Collectors.toList());
    }

    public List<ArticleResponse> getLatestFollowingArticles(int page, int size, String jwt) {
        logger.info("Fetching latest following articles, page: " + page + ", size: " + size);
        UserDetailsDTO userDetails = authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_USER, Constants.ROLE_ADMIN);
        AuthorDTO author = authorService.getAuthorById(userDetails.getAuthorId());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt"));
        return articleRepository.findAllByAuthorIdInOrderByCreateAt(author.getFollowing(), pageRequest)
                .getContent()
                .stream()
                .map(article -> articleResponseFactory.createResponse(article, userDetails.getAuthorId()))
                .collect(Collectors.toList());
    }

    public ArticleDetailsResponse getArticle(int articleId, String jwt) {
        logger.info("Fetching article by ID: " + articleId);
        UserDetailsDTO userDetails = authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_USER, Constants.ROLE_ADMIN);

        Articles articleWithLikes = articleRepository.findByIdWithLikes(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));

        Articles articleWithArticles = articleRepository.findByIdWithArticles(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));

        ArticleDetailsResponse response = articleResponseFactory.createResponseForOneArticle(articleWithLikes, userDetails.getAuthorId());

        response.setComments(articleWithArticles.getArticles()
                .stream()
                .map(comment -> articleResponseFactory.createResponse(comment, userDetails.getAuthorId()))
                .collect(Collectors.toList()));

        return response;
    }

    public List<ArticleDTO> getArticlesByAuthorId(int authorId) {
        logger.info("Fetching articles by author ID: " + authorId);
        return articleRepository.findAllByAuthorIdOrderByCreateAt(authorId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ArticleDTO getArticleById(int articleId) {
        logger.info("Fetching article by ID: " + articleId);
        return articleRepository.findById(articleId)
                .map(this::toDTO)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));
    }

    public void deleteArticleById(int articleId, String jwt) {
        logger.info("Deleting article by ID: " + articleId);
        UserDetailsDTO userDetails = authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_USER, Constants.ROLE_ADMIN);
        Articles article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));

        if (canDeleteArticle(article.getAuthorId(), userDetails.getAuthorId(), userDetails.getRole())) {
            articleRepository.deleteById(articleId);
            logger.info("Article deleted successfully");
        } else {
            handleUnauthorizedDeleteAttempt(userDetails.getUsername());
        }
    }

    public void deleteArticlesByAuthorId(int authorId) {
        logger.info("Deleting articles by author ID: " + authorId);
        articleRepository.deleteAllByAuthorId(authorId);
        logger.info("Articles deleted successfully for author ID: " + authorId);
    }

    private void removeLike(Articles article, int userId) {
        Like like = article.getLikes().stream().filter(l -> l.getAuthorId() == userId).findFirst()
                .orElseThrow(() -> new ArticleNotFoundException("Like not found"));
        article.getLikes().remove(like);
        articleRepository.save(article);
        logger.info("Removed like for article ID: " + article.getId() + " by user ID: " + userId);
    }

    private void addLike(Articles article, int userId) {
        Like newLike = from(userId, article);
        article.getLikes().add(newLike);
        articleRepository.save(article);

     //   sendLikeMessage(userId, article.getId());
        logger.info("Added like for article ID: " + article.getId() + " by user ID: " + userId);
    }

    private ArticleDTO toDTO(Articles article) {
        return new ArticleDTO(
                article.getId(),
                article.getCreateAt().toString(),
                article.getContent(),
                article.getAuthorId()
        );
    }

    private Articles from(ArticleCreateRequest request, UserDetailsDTO userDetails, Articles motherArticle) {
        return new Articles(
                0,
                new Timestamp(System.currentTimeMillis()),
                request.getText(),
                userDetails.getAuthorId(),
                motherArticle
        );
    }

    private Like from(int likerId, Articles likedArticle) {
        return new Like(
                0,
                new Timestamp(System.currentTimeMillis()),
                likerId,
                likedArticle
        );
    }

    private Articles from(ArticleUpdateRequest request, Articles articleToUpdate, UserDetailsDTO userDetails) {
        return new Articles(
              articleToUpdate.getId(),
                articleToUpdate.getCreateAt(),
                request.getText(),
                userDetails.getAuthorId(),
                null  // Assuming no mother article update
        );
    }

    @Cacheable(cacheNames = "userLikedArticles", key = "#userId")
    public List<ArticleDTO> getLikedArticlesForUser(int userId) {
        logger.info("Fetching liked articles for user based on userId");
        List<Articles> articles = articleRepository.findLikedArticlesByUserId(userId);
        return articles.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


  /*  private void sendArticleMessage(Articles created) {
        ArticleMessage message = new ArticleMessage(
                new Timestamp(System.currentTimeMillis()),
                created.getId(),
                created.getAuthorId()
        );
        kafkaLikeService.sendArticleMessage(message);
    }*/

/*    private void sendLikeMessage(int authorId, int articleId) {
        LikeMessage message = new LikeMessage(
                new Timestamp(System.currentTimeMillis()),
                authorId,
                articleId
        );
        kafkaLikeService.sendLikeMessage(message);
    }*/

    private boolean canUpdateArticle(int articleAuthorId, int userId) {
        return articleAuthorId == userId;
    }

    private boolean canDeleteArticle(int articleAuthorId, int userId, String userRole) {
        return articleAuthorId == userId || Constants.ROLE_ADMIN.equals(userRole);
    }

    private void handleUnauthorizedUpdateAttempt(String username) {
        logger.warn("Unauthorized attempt to update article by user: " + username);
        throw new UnauthorizedException("You are not authorized to update this article!");
    }

    private void handleUnauthorizedDeleteAttempt(String username) {
        logger.warn("Unauthorized attempt to delete article by user: " + username);
        throw new UnauthorizedException("You are not authorized to delete this article!");
    }
}
