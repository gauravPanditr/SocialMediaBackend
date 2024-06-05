package com.example.ArticlesServices.external;

import com.example.Common.client.AuthorClient;
import com.example.Common.exception.AuthorApiException;
import com.example.Common.model.AuthorDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class AuthorApiServiceImpl implements AuthorApiService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorApiServiceImpl.class);

    private final AuthorClient authorClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthorApiServiceImpl(AuthorClient authorClient, ObjectMapper objectMapper) {
        this.authorClient = authorClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public AuthorDTO getAuthorById(int authorId) {
        logger.info("Getting author by ID: " + authorId);
        ResponseEntity<String> response = authorClient.getAuthorById(authorId);
        return deserializeAuthor(response);
    }

    private AuthorDTO deserializeAuthor(ResponseEntity<String> response) {
        String responseBody = response.getBody();
        if (responseBody == null) {
            throw new AuthorApiException("Author API response body is null");
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new AuthorApiException("Author API request failed with status code: " + response.getStatusCode());
        }

        try {
            AuthorDTO authorDTO = objectMapper.readValue(responseBody, AuthorDTO.class);
            logger.debug("Deserialized author: " + authorDTO);
            return authorDTO;
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing AuthorDTO", e);
            throw new AuthorApiException("Error deserializing AuthorDTO", e);
        }
    }
}
