package com.example.AuthorServices.service;



import com.example.AuthorServices.dtos.AuthorRequest;
import com.example.AuthorServices.enities.Author;
import com.example.AuthorServices.enities.Follow;
import com.example.AuthorServices.exception.AuthorNotFoundException;
import com.example.AuthorServices.external.AuthorizationApiService;
import com.example.AuthorServices.repository.AuthorRepository;
import com.example.AuthorServices.repository.FollowRepository;
import com.example.Common.Constants;
import com.example.Common.model.AuthorDTO;
import com.example.Common.model.UserDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Auth implements AuthorService {

    private final AuthorRepository authorRepository;
    private final FollowRepository followRepository;
   // private final ArticleApiService articleService;
    private final AuthorizationApiService authorizationService;
   // private final KafkaLikeService kafkaLikeService;

    private final Logger logger = LoggerFactory.getLogger(Auth.class);

    public Auth(AuthorRepository authorRepository,
                             FollowRepository followRepository,

                             AuthorizationApiService authorizationService
                         ) {
        this.authorRepository = authorRepository;
        this.followRepository = followRepository;

        this.authorizationService = authorizationService;

    }

    @Override
    public List<AuthorDTO> getAllAuthors(String jwt) {
        logger.info("Fetching all authors");
        authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_ADMIN);
        List<Author> authors = authorRepository.findAll();
        List<AuthorDTO> authorDTOs = authors.stream().map(this::toDTO).collect(Collectors.toList());
        logger.info("Successfully fetched all authors");
        return authorDTOs;
    }

    @Override
    public List<AuthorDTO> getFollowing(int authorId) {
        logger.info("Fetching following for author ID: " + authorId);
        List<Author> followingAuthors = authorRepository.findAllFollowing(authorId);
        List<AuthorDTO> authorDTOs = followingAuthors.stream().map(this::toDTO).collect(Collectors.toList());
        logger.info("Successfully fetched following authors");
        return authorDTOs;
    }

    @Override
    public List<AuthorDTO> getFollowers(int authorId) {
        logger.info("Fetching followers for author ID: " + authorId);
        List<Author> followerAuthors = authorRepository.findAllFollowers(authorId);
        List<AuthorDTO> authorDTOs = followerAuthors.stream().map(this::toDTO).collect(Collectors.toList());
        logger.info("Successfully fetched followed authors");
        return authorDTOs;
    }

    @Override
    public AuthorDTO getAuthorById(int authorId) {
        logger.info("Fetching author by ID: " + authorId);
        Optional<Author> authorOptional = authorRepository.findById(authorId);
        Author author = authorOptional.orElseThrow(() -> new AuthorNotFoundException("Author not found with ID: " + authorId));
        AuthorDTO authorDTO = toDTO(author);
        logger.info("Successfully fetched author by ID: " + authorId);
        return authorDTO;
    }

    @Override
    public AuthorDTO getAuthorByUsername(String username) {
        logger.info("Fetching author by username: " + username);
        Optional<Author> authorOptional = authorRepository.findAuthorByUsername(username);
        Author author = authorOptional.orElseThrow(() -> new AuthorNotFoundException("Author not found with username: " + username));
        AuthorDTO authorDTO = toDTO(author);
        logger.info("Successfully fetched author by username: " + username);
        return authorDTO;
    }


    @Override
    public void saveAuthor(AuthorRequest request) {
        logger.info("Saving author: " + request);
        Author author = from(request);
        authorRepository.save(author);
        logger.info("Successfully saved author: " + author);
    }

    @Override
    public void followAuthor(int followingId, String jwt) {
      UserDetailsDTO userDetails = authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_USER);
        Author followerAuthor = getAuthorById(userDetails.getAuthorId(), "Follower author not found");
        Author followingAuthor = getAuthorById(followingId, "Following author not found");
        validateFollowRelationshipNotExist(followerAuthor, followingAuthor);

        Follow follow = from(followerAuthor, followingAuthor);
        followRepository.save(follow);
      //  sendFollowMessage(followerAuthor.getId(), followingAuthor.getId());

        logger.info("Successfully created follow relationship. Follower: " + followerAuthor.getUsername() +
                ", Following: " + followingAuthor.getUsername());
    }

    @Override
    public void unfollowAuthor(int followingId, String jwt) {
        UserDetailsDTO  userDetails = authorizationService.getUserDetailsAndValidate(jwt, Constants.ROLE_USER);
        followRepository.deleteByFollowerIdAndFollowingId(userDetails.getAuthorId(), followingId);

        logger.info("Successfully deleted follow relationship. Follower: " + userDetails.getUsername() +
                ", Following: " + followingId);
    }

    @Override
    public void deleteAuthorById(int authorId) {
        logger.info("Deleting author by ID: " + authorId);
        authorRepository.deleteById(authorId);
      //  articleService.deleteArticlesByAuthorId(authorId);
        logger.info("Successfully deleted author by ID: " + authorId);
    }

    private Author getAuthorById(int authorId, String errorMessage) {
        Optional<Author> authorOptional = authorRepository.findById(authorId);
        return authorOptional.orElseThrow(() -> new AuthorNotFoundException(errorMessage));
    }

    private AuthorDTO toDTO(Author author) {
        return new AuthorDTO(
                author.getId(),
                author.getFirstName(),
                author.getLastName(),
                author.getUsername(),
                author.getFollowing().stream().map(f -> f.getFollowing().getId()).collect(Collectors.toList()),
                author.getFollowedBy().stream().map(f -> f.getFollower().getId()).collect(Collectors.toList())
        );
    }

    private Follow from(Author follower, Author following) {
        return new Follow(0, follower, following, Timestamp.from(Instant.now()));
    }

    private Author from(AuthorRequest request) {
        return new Author(0, request.getFirstName(), request.getLastName(), request.getUsername());
    }

   /* private void sendFollowMessage(int followerId, int followedId) {
        FollowMessage message = new FollowMessage(new Timestamp(System.currentTimeMillis()), followerId, followedId);
        kafkaLikeService.sendFollowMessage(message);
    }
*/
    private boolean isFollow(Author follower, Author followed) {
        return follower.getFollowing().stream().anyMatch(f -> f.getFollowing().equals(followed));
    }

    private boolean isBeingFollowed(Author follower, Author followed) {
        return followed.getFollowedBy().stream().anyMatch(f -> f.getFollower().equals(follower));
    }

    private void validateFollowRelationshipNotExist(Author follower, Author following) {
        if (isFollow(follower, following) || isBeingFollowed(following, follower)) {
            throw new IllegalStateException("Follow relationship already exists!");
        }
    }
}
