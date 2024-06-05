package com.example.AuthorizationServices.external;


import com.example.Common.client.AuthorClient;
import com.example.Common.exception.AuthorApiException;
import com.example.Common.model.AuthorDTO;
import com.example.Common.model.AuthorRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class AuthorApiServiceImpl implements AuthorApiService {

    private final AuthorClient authorClient;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AuthorApiServiceImpl(AuthorClient authorClient, ObjectMapper objectMapper) {
        this.authorClient = authorClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public AuthorDTO getAuthorByUsername(String username) {
        logger.info("Getting author by username: " + username);
        ResponseEntity<String> response = authorClient.getAuthorByUsername(username);
        return deserializeAuthor(response);
    }

    @Override
    public void createAuthor(AuthorRequest authorRequest) {
        logger.info("Creating author: " + authorRequest);
        authorClient.createAuthor(authorRequest);
        logger.info("Author created successfully: " + authorRequest);
    }

    @Override
    public void deleteAuthorById(int id) {
        logger.info("Deleting author by ID: " + id);
        authorClient.deleteAuthorById(id);
        logger.info("Author deleted successfully: ID " + id);
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
        } catch (Exception e) {
            logger.error("Error deserializing AuthorDTO", e);
            throw new AuthorApiException("Error deserializing AuthorDTO", e);
        }
    }
}
