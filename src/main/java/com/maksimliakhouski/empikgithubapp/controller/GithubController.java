package com.maksimliakhouski.empikgithubapp.controller;

import com.maksimliakhouski.empikgithubapp.dto.GithubUser;
import com.maksimliakhouski.empikgithubapp.service.GithubUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class GithubController {

    private final Logger logger = LoggerFactory.getLogger(GithubController.class);
    private final GithubUserService githubUserService;

    public GithubController(GithubUserService githubUserService) {
        this.githubUserService = githubUserService;
    }

    @GetMapping("/{login}")
    public GithubUser getGithubUser(@PathVariable(name = "login") String login) {
        logger.info("GET request to /users/{login} with login: " + login);
        return githubUserService.getGithubUser(login);
    }

}
