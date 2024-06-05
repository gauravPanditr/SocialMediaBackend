package com.example.AuthorizationServices.controller;

import com.example.AuthorizationServices.dtos.LoginRequest;
import com.example.AuthorizationServices.dtos.LoginResponse;
import com.example.AuthorizationServices.dtos.RegisterRequest;
import com.example.AuthorizationServices.service.AuthorizationService;
import com.example.Common.Constants;
import com.example.Common.model.UserDetailsDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
public class AuthController {

   private final AuthorizationService authorizationService;

    public AuthController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
    @GetMapping("/")
    public String helloWorld() {
        return "Hello World!";
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterRequest request) {
        authorizationService.registerUser(request);
        System.out.println("Hello");

        // Create a response entity with an appropriate HTTP status and any data you want to return
        return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful");
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorizationService.loginUser(request));
    }

    @PostMapping(value = "/refresh-token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorizationService.refreshAccessToken(jwt));
    }

    @GetMapping(value = "/user-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDetailsDTO> getUserDetails(@RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(authorizationService.getUserDetails(jwt));
    }

    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@RequestHeader(Constants.AUTHORIZATION_HEADER) String jwt) {
         authorizationService.deleteUser(jwt);
    }
}

