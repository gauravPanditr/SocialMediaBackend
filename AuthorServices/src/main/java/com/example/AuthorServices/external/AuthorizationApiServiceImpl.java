package com.example.AuthorServices.external;

import com.example.Common.client.AuthorizationClient;
import com.example.Common.exception.AuthorizationApiException;
import com.example.Common.exception.UnauthorizedException;
import com.example.Common.model.UserDetailsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class AuthorizationApiServiceImpl implements AuthorizationApiService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthorizationApiServiceImpl.class);

    private final AuthorizationClient authorizationClient;
    private final ObjectMapper objectMapper;

    public AuthorizationApiServiceImpl(AuthorizationClient authorizationClient, ObjectMapper objectMapper) {
        this.authorizationClient = authorizationClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public UserDetailsDTO getUserDetailsAndValidate(String jwt, String... roles) {
        logger.info("Getting user details for JWT: {}", jwt);
        ResponseEntity<String> response = authorizationClient.getUserDetails(jwt);

        UserDetailsDTO userDetails = deserializeUserDetails(response);
        if (hasAnyRole(userDetails.getRole(), roles)) {
            return userDetails;
        }
        throw new UnauthorizedException("You are not authorized to do this!");
    }

    private UserDetailsDTO deserializeUserDetails(ResponseEntity<String> response) {
        String responseBody = Objects.requireNonNull(response.getBody(), "Authorization API response body is null");

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new AuthorizationApiException("Authorization API request failed with status code: " + response.getStatusCodeValue());
        }

        try {
            UserDetailsDTO userDetailsDTO = objectMapper.readValue(responseBody, UserDetailsDTO.class);
            logger.debug("Deserialized user details: {}", userDetailsDTO);
            return userDetailsDTO;
        } catch (Exception e) {
            logger.error("Error deserializing UserDetailsDTO", e);
            throw new AuthorizationApiException("Error deserializing UserDetailsDTO", e);
        }
    }

    private boolean hasAnyRole(String userRole, String... roles) {
        for (String role : roles) {
            if (userRole.equals(role)) {
                return true;
            }
        }
        return false;
    }
}
