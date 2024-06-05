package com.example.AuthorizationServices.service;

import com.example.AuthorizationServices.constant.SecurityConstants;
import com.example.AuthorizationServices.dtos.LoginRequest;
import com.example.AuthorizationServices.dtos.LoginResponse;
import com.example.AuthorizationServices.dtos.RegisterRequest;
import com.example.AuthorizationServices.enities.User;
import com.example.AuthorizationServices.exception.UserNotFoundException;
import com.example.AuthorizationServices.exception.UsernameAlreadyExistsException;
import com.example.AuthorizationServices.exception.WrongCredentialsException;
import com.example.AuthorizationServices.external.AuthorApiService;
import com.example.AuthorizationServices.repository.UserRepository;
import com.example.Common.model.AuthorDTO;
import com.example.Common.model.AuthorRequest;
import com.example.Common.model.UserDetailsDTO;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.Optional;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserRepository userRepository;
    private final AuthorApiService authorApiService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public AuthorizationServiceImpl(UserRepository userRepository, AuthorApiService authorApiService,
                                    JwtService jwtService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorApiService = authorApiService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerUser(RegisterRequest request) {
        logger.info("Initiating user registration process");
        validateUsernameAvailability(request.getUsername());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = from(request, hashedPassword);
        userRepository.save(newUser);

        AuthorRequest authorRequest = from(request);
        authorApiService.createAuthor(authorRequest);

        logger.info("User registered successfully: {}", request.getUsername());
    }

    @Override
    public LoginResponse loginUser(LoginRequest request) {
        logger.info("Initiating user login process");
        User user = getUserByUsername(request.getUsername());
        validatePasswords(request.getPassword(), user.getPassword());
        AuthorDTO author = authorApiService.getAuthorByUsername(request.getUsername());

        long tokenExpirationDate = Instant.now().toEpochMilli() + SecurityConstants.JWT_EXPIRE_TIME;
        long refreshTokenExpirationDate = Instant.now().toEpochMilli() + SecurityConstants.REFRESH_TOKEN_EXPIRE_TIME;
        String token = jwtService.buildJwt(user, tokenExpirationDate);
        String refreshToken = jwtService.buildJwt(user, refreshTokenExpirationDate);

        logger.info("User logged in successfully: {}", request.getUsername());
        return createLoginResponse(author, user, token, refreshToken, tokenExpirationDate, refreshTokenExpirationDate);
    }

    @Override
    public LoginResponse refreshAccessToken(String jwt) {
        logger.info("Initiating refresh token process");

        Claims claims = jwtService.parseJwtClaims(jwt);
        String username = claims.get("username").toString();
        User user = getUserByUsername(username);
        AuthorDTO author = authorApiService.getAuthorByUsername(username);

        long tokenExpirationDate = Instant.now().toEpochMilli() + SecurityConstants.JWT_EXPIRE_TIME;
        long refreshTokenExpirationDate = Instant.now().toEpochMilli() + SecurityConstants.REFRESH_TOKEN_EXPIRE_TIME;
        String token = jwtService.buildJwt(user, tokenExpirationDate);
        String refreshToken = jwtService.buildJwt(user, refreshTokenExpirationDate);

        logger.info("Token refreshed successfully for: {}", username);
        return createLoginResponse(author, user, token, refreshToken, tokenExpirationDate, refreshTokenExpirationDate);
    }

    @Override
    public UserDetailsDTO getUserDetails(String jwt) {
        logger.info("Initiating get user details process");
        Claims claims = jwtService.parseJwtClaims(jwt);
        String username = claims.get("username").toString();
        String role = claims.get("role").toString();
        AuthorDTO author = authorApiService.getAuthorByUsername(username);

        return createUserDetails(author, role);
    }

    @Transactional
    @Override
    public void deleteUser(String jwt) {
        logger.info("Initiating delete user process");
        UserDetailsDTO user = getUserDetails(jwt);
        userRepository.deleteByUsername(user.getUsername());
        authorApiService.deleteAuthorById(user.getAuthorId());
        logger.info("User deleted successfully: {}", user.getUsername());
    }

    private User getUserByUsername(String username) {
        Optional<User> userOptional =userRepository.findUserByUsername(username);
        return userOptional.orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }


    private User from(RegisterRequest request, String password) {
        return new User(request.getUsername(), password, request.getRole());
    }

    private AuthorRequest from(RegisterRequest request) {
        return new AuthorRequest(request.getFirstName(), request.getLastName(), request.getUsername());
    }

    private UserDetailsDTO createUserDetails(AuthorDTO author, String role) {
        return new UserDetailsDTO(author.getId(), author.getFirstName(), author.getLastName(), author.getUsername(), role);
    }

    private LoginResponse createLoginResponse(AuthorDTO author, User user, String token, String refreshToken,
                                              long tokenExpirationDate, long refreshTokenExpirationDate) {
        return new LoginResponse(author.getId(), user.getUsername(), author.getFirstName(), author.getLastName(),
                author.getFollowing().size(), author.getFollowers().size(), token, refreshToken, user.getRole(),
                tokenExpirationDate, refreshTokenExpirationDate);
    }

    private void validatePasswords(String passwordProvided, String passwordRegistered) {
        if (!passwordEncoder.matches(passwordProvided, passwordRegistered)) {
            throw new WrongCredentialsException("Invalid password!");
        }
    }

    private void validateUsernameAvailability(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("User with this username already exists!");
        }
    }
}
