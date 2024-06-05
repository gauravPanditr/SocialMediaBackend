package com.example.ArticlesServices.services;


import com.example.ArticlesServices.dtos.ArticleDetailsResponse;
import com.example.ArticlesServices.dtos.ArticleResponse;
import com.example.ArticlesServices.dtos.AuthorResponse;
import com.example.ArticlesServices.dtos.LikeDetailsResponse;
import com.example.ArticlesServices.enities.Articles;
import com.example.ArticlesServices.external.AuthorApiService;
import com.example.ArticlesServices.repository.ArticleRepository;
import com.example.Common.model.AuthorDTO;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class ArticleResponseFactory {

    private final AuthorApiService authorService;
    private final ArticleRepository articleRepository;

    public ArticleResponseFactory(AuthorApiService authorService, ArticleRepository articleRepository) {
        this.authorService = authorService;
        this.articleRepository = articleRepository;
    }

    public ArticleResponse createResponse(Articles theArticle, int userId) {
        AuthorResponse author = getAuthorResponse(theArticle.getAuthorId(), userId);
        List<String> likerFullNames = getLikerFullNames(theArticle);
        int comments = articleRepository.countAllByMotherArticleId(theArticle.getId());

        return new ArticleResponse(
                theArticle.getId(),
                theArticle.getContent(),
                theArticle.getCreateAt(),
                getTimeElapsed(theArticle.getCreateAt()),
                formatDateTime(theArticle.getCreateAt()),
                author,
                new LikeDetailsResponse(likerFullNames),
                comments
        );
    }

    public ArticleDetailsResponse createResponseForOneArticle(Articles theArticle, int userId) {
        AuthorResponse author = getAuthorResponse(theArticle.getAuthorId(), userId);
        List<String> likerFullNames = getLikerFullNames(theArticle);

        return new ArticleDetailsResponse(
                theArticle.getId(),
                theArticle.getContent(),
                theArticle.getCreateAt(),
                getTimeElapsed(theArticle.getCreateAt()),
                formatDateTime(theArticle.getCreateAt()),
                author,
                new LikeDetailsResponse(likerFullNames),
                List.of()
        );
    }

    private AuthorResponse getAuthorResponse(int authorId, int userId) {
        AuthorDTO author = authorService.getAuthorById(authorId);
        return new AuthorResponse(
                author.getId(),
                author.getUsername(),
                author.getFirstName(),
                author.getLastName(),
                author.getFollowers().contains(userId)
        );
    }

    private List<String> getLikerFullNames(Articles theArticle) {
        return theArticle.getLikes().stream()
                .map(like -> {
                    var liker = authorService.getAuthorById(like.getAuthorId());
                    return liker.getFirstName() + " " + liker.getLastName();
                })
                .collect(Collectors.toList());
    }

    private String getTimeElapsed(Timestamp timestamp) {
        long now = Timestamp.from(Instant.now()).getTime();
        long timeDiff = now - timestamp.getTime();

        if (timeDiff < 60000) return (timeDiff / 1000) + "s";
        if (timeDiff < 3600000) return (timeDiff / 60000) + "m";
        if (timeDiff < 86400000) return (timeDiff / 3600000) + "h";
        if (timeDiff < 604800000) return (timeDiff / 86400000) + "d";
        return (timeDiff / 604800000) + "w";
    }

    private String formatDateTime(Timestamp inputDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(inputDate.getTime());

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String month = new SimpleDateFormat("MMM", Locale.US).format(inputDate);
        int year = calendar.get(Calendar.YEAR);

        String formattedTime = formatTime(hour, minute);
        String formattedDate = month + " " + day + ", " + year;

        return formattedTime + " · " + formattedDate;
    }

    private String formatTime(int hour, int minute) {
        boolean isPM = hour >= 12;
        int formattedHour = hour % 12;
        String period = isPM ? "PM" : "AM";

        return String.format("%02d:%02d %s", (formattedHour == 0) ? 12 : formattedHour, minute, period);
    }
}
