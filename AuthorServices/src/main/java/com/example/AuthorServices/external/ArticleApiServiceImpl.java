package com.example.AuthorServices.external;

import com.example.Common.client.ArticleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ArticleApiServiceImpl implements ArticleApiService {

    private final Logger logger = LoggerFactory.getLogger(ArticleApiServiceImpl.class);

    private final ArticleClient articleClient;

    @Autowired
    public ArticleApiServiceImpl(ArticleClient articleClient) {
        this.articleClient = articleClient;
    }

    @Override
    public void deleteArticlesByAuthorId(int authorId) {
        logger.info("Deleting articles by author ID: " + authorId);
        articleClient.deleteArticlesByAuthorId(authorId);
        logger.info("Articles deleted successfully for author ID: " + authorId);
    }
}
