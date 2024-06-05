package com.example.ArticlesServices.external;

import com.example.Common.client.AuthorizationClient;
import com.example.Common.exception.AuthorizationApiException;
import com.example.Common.exception.UnauthorizedException;
import com.example.Common.model.UserDetailsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class AuthorizationApiServiceImpl implements AuthorizationApiService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationApiServiceImpl.class);

    private final AuthorizationClient authorizationClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthorizationApiServiceImpl(AuthorizationClient authorizationClient, ObjectMapper objectMapper) {
        this.authorizationClient = authorizationClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public UserDetailsDTO getUserDetailsAndValidate(String jwt, String... roles) {
        logger.info("Getting user details for JWT: " + jwt);
        ResponseEntity<String> response = authorizationClient.getUserDetails(jwt);

        UserDetailsDTO userDetails = deserializeUserDetails(response);
        if (roles != null && roles.length > 0 && containsRole(userDetails, roles)) {
            return userDetails;
        }
        throw new UnauthorizedException("You are not authorized to do this!");
    }

    private boolean containsRole(UserDetailsDTO userDetails, String[] roles) {
        for (String role : roles) {
            if (role.equals(userDetails.getRole())) {
                return true;
            }
        }
        return false;
    }

    private UserDetailsDTO deserializeUserDetails(ResponseEntity<String> response) {
        String responseBody = response.getBody();
        if (responseBody == null) {
            throw new AuthorizationApiException("Authorization API response body is null");
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new AuthorizationApiException("Authorization API request failed with status code: " + response.getStatusCode());
        }

        try {
            UserDetailsDTO userDetailsDTO = objectMapper.readValue(responseBody, UserDetailsDTO.class);
            logger.debug("Deserialized user details: " + userDetailsDTO);
            return userDetailsDTO;
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing UserDetailsDTO", e);
            throw new AuthorizationApiException("Error deserializing UserDetailsDTO", e);
        }
    }
}
