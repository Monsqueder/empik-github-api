package com.maksimliakhouski.empikgithubapp.service;

import com.maksimliakhouski.empikgithubapp.dto.GithubResponseDto;
import com.maksimliakhouski.empikgithubapp.dto.GithubUser;
import com.maksimliakhouski.empikgithubapp.exception.RateLimitException;
import com.maksimliakhouski.empikgithubapp.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubUserService {

    @Value("${api.github.url}")
    private String GITHUB_URL;
    @Value("${api.github.key}")
    private String TOKEN;

    private final Logger logger = LoggerFactory.getLogger(GithubUserService.class);
    private final RestTemplate restTemplate;
    private final RequestCounterService requestCounterService;

    public GithubUserService(RestTemplate restTemplate, RequestCounterService requestCounterService) {
        this.restTemplate = restTemplate;
        this.requestCounterService = requestCounterService;
    }

    public GithubUser getGithubUser(String login) {
        try {
            GithubResponseDto githubResponseDto = getGithubResponseDtoFromApi(login);
            logger.info("Concurrent execution of incrementing request count with login: " + login.toLowerCase());
            Thread.ofVirtual().start(() -> requestCounterService.incrementRequestCount(login.toLowerCase()));
            return mapGithubUser(githubResponseDto);
        } catch (HttpClientErrorException e) {
            logger.info("HttpClientErrorException has been thrown!");
            if (e.getStatusCode().value() == 404) throw new UserNotFoundException(login);
            if (e.getStatusCode().value() == 403) {
                logger.info("Exceeded api rate limit! Please, provide valid token to application.properties!");
                throw new RateLimitException();
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    private GithubUser mapGithubUser(GithubResponseDto githubResponseDto) {
        return new GithubUser(
                githubResponseDto.id(),
                githubResponseDto.login(),
                githubResponseDto.name(),
                githubResponseDto.type(),
                githubResponseDto.avatarUrl(),
                githubResponseDto.createdAt(),
                calculateRating(githubResponseDto.followers(), githubResponseDto.publicRepos())
        );
    }

    private GithubResponseDto getGithubResponseDtoFromApi(String login) throws HttpClientErrorException {
        logger.info("Sending request to Github...");
        GithubResponseDto githubResponseDto;
        HttpHeaders headers = new HttpHeaders();
        if (TOKEN != null && !TOKEN.isBlank()) headers.set("Authorization", "Bearer " + TOKEN);
        HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);
        githubResponseDto = restTemplate.exchange(
                GITHUB_URL + login,
                HttpMethod.GET,
                httpEntity,
                GithubResponseDto.class
        ).getBody();
        return githubResponseDto;
    }

    private double calculateRating(int followersCount, int publicReposCount) {
        if (followersCount == 0) return 0;
        return (double) 6 / followersCount * (2 + publicReposCount);
    }
}
