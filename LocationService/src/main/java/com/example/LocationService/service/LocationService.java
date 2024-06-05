package com.example.LocationService.service;

import com.example.Common.exception.UnauthorizedException;
import com.example.Common.model.LocationRequest;
import com.example.Common.model.UserDetailsDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationService {

    private static final String USER_SERVICE_URL = "http://localhost:8000/api/user-info";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RestTemplate restTemplate;

    public LocationRequest createLocationRequest(Double latitude, Double longitude) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header is missing or invalid");
        }

        String token = authorizationHeader.substring(7);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UserDetailsDTO userDetails;
        try {
            ResponseEntity<UserDetailsDTO> response = restTemplate.exchange(
                    USER_SERVICE_URL, HttpMethod.GET, entity, UserDetailsDTO.class);
            userDetails = response.getBody();
        } catch (HttpClientErrorException e) {
            throw new UnauthorizedException("User authorization failed", e);
        }

        if (userDetails == null) {
            throw new UnauthorizedException("Failed to fetch user details");
        }

        return new LocationRequest(userDetails.getAuthorId(), latitude, longitude);
    }
}
