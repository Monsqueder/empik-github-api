package com.maksimliakhouski.empikgithubapp.dto;

public record GithubUser(
        String id,
        String login,
        String name,
        String type,
        String avatarUrl,
        String createdAt,
        double calculations) {
}
