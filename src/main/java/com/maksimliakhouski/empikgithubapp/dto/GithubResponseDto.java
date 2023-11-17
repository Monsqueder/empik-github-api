package com.maksimliakhouski.empikgithubapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubResponseDto(
        String id,
        String login,
        String name,
        String type,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("created_at") String createdAt,
        int followers,
        @JsonProperty("public_repos") int publicRepos) {
}
