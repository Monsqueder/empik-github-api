package com.maksimliakhouski.empikgithubapp.unit;

import com.maksimliakhouski.empikgithubapp.dto.GithubResponseDto;
import com.maksimliakhouski.empikgithubapp.dto.GithubUser;
import com.maksimliakhouski.empikgithubapp.service.GithubUserService;
import com.maksimliakhouski.empikgithubapp.service.RequestCounterService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.maksimliakhouski.empikgithubapp.util.TestUtils.prepareSampleGithubResponseDto;
import static com.maksimliakhouski.empikgithubapp.util.TestUtils.prepareSampleGithubUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GithubUserServiceTest {

    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final RequestCounterService requestCounterService = mock(RequestCounterService.class);

    private final GithubUserService githubUserService = new GithubUserService(restTemplate, requestCounterService);

    @Test
    public void testGetGithubUser() {
        GithubResponseDto githubResponseDto = prepareSampleGithubResponseDto();
        GithubUser user = prepareSampleGithubUser();
        String login = githubResponseDto.login();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GithubResponseDto.class)))
                .thenReturn(ResponseEntity.ok(githubResponseDto));

        GithubUser retrievedUser = githubUserService.getGithubUser(login);

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GithubResponseDto.class));
        verify(requestCounterService, times(1)).incrementRequestCount(login);

        assertThat(retrievedUser).usingRecursiveComparison().isEqualTo(user);
    }
}
