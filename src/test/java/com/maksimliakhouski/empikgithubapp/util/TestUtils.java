package com.maksimliakhouski.empikgithubapp.util;

import com.maksimliakhouski.empikgithubapp.dto.GithubResponseDto;
import com.maksimliakhouski.empikgithubapp.dto.GithubUser;

public class TestUtils {

    public static GithubUser prepareSampleGithubUser() {
        return new GithubUser(
                "12345",
                "user",
                "John",
                "user",
                "https:/this.is.not.real.url",
                "23.04.2002",
                10.0
        );
    }

    public static GithubResponseDto prepareSampleGithubResponseDto() {
        return new GithubResponseDto(
                "12345",
                "user",
                "John",
                "user",
                "https:/this.is.not.real.url",
                "23.04.2002",
                6,
                8
        );
    }

}
