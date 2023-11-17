package com.maksimliakhouski.empikgithubapp.integration;

import com.maksimliakhouski.empikgithubapp.dto.GithubResponseDto;
import com.maksimliakhouski.empikgithubapp.dto.GithubUser;
import com.maksimliakhouski.empikgithubapp.model.RequestCounter;
import com.maksimliakhouski.empikgithubapp.repository.RequestCounterRepository;
import com.maksimliakhouski.empikgithubapp.service.GithubUserService;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.maksimliakhouski.empikgithubapp.util.TestUtils.prepareSampleGithubResponseDto;
import static com.maksimliakhouski.empikgithubapp.util.TestUtils.prepareSampleGithubUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubUserServiceIntegrationTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private GithubUserService githubUserService;
    @Autowired
    private RequestCounterRepository requestCounterRepository;

    @BeforeEach
    public void setUp() {
        requestCounterRepository.deleteAll();
    }

    @Test
    public void testFlowWithExistingLogin() {
        GithubUser githubUser = prepareSampleGithubUser();
        GithubResponseDto githubResponseDto = prepareSampleGithubResponseDto();

        RequestCounter requestCounter = new RequestCounter();
        requestCounter.setLogin(githubUser.login());
        requestCounter.setRequestCount(1);

        requestCounterRepository.save(requestCounter);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GithubResponseDto.class)))
                .thenReturn(ResponseEntity.ok(githubResponseDto));

        GithubUser retrievedGithubUser = githubUserService.getGithubUser(githubUser.login());

        assertThat(retrievedGithubUser).usingRecursiveComparison().isEqualTo(githubUser);

        await().atMost(Duration.FIVE_SECONDS).until(() -> {
            RequestCounter retrievedRequestCounter = requestCounterRepository.findAll().get(0);
            return retrievedRequestCounter.getRequestCount() == 2;
        });

        List<RequestCounter> retrievedRequestCounterList = requestCounterRepository.findAll();

        assertThat(retrievedRequestCounterList.size()).isEqualTo(1);
        assertThat(retrievedRequestCounterList.get(0).getLogin()).isEqualTo(githubUser.login());
        assertThat(retrievedRequestCounterList.get(0).getRequestCount()).isEqualTo(2);
    }

    @Test
    public void testFlowWithNotExistingLogin() {
        GithubUser githubUser = prepareSampleGithubUser();
        GithubResponseDto githubResponseDto = prepareSampleGithubResponseDto();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GithubResponseDto.class)))
                .thenReturn(ResponseEntity.ok(githubResponseDto));

        GithubUser retrievedGithubUser = githubUserService.getGithubUser(githubUser.login());

        assertThat(retrievedGithubUser).usingRecursiveComparison().isEqualTo(githubUser);

        await().atMost(Duration.FIVE_SECONDS).until(() -> !requestCounterRepository.findAll().isEmpty());

        List<RequestCounter> retrievedRequestCounterList = requestCounterRepository.findAll();

        assertThat(retrievedRequestCounterList.size()).isEqualTo(1);
        assertThat(retrievedRequestCounterList.get(0).getLogin()).isEqualTo(githubUser.login());
        assertThat(retrievedRequestCounterList.get(0).getRequestCount()).isEqualTo(1);
    }

    @Test
    public void testFlowAtomicity() throws InterruptedException {
        GithubUser githubUser = prepareSampleGithubUser();
        GithubResponseDto githubResponseDto = prepareSampleGithubResponseDto();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(GithubResponseDto.class)))
                .thenReturn(ResponseEntity.ok(githubResponseDto));

        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        final int threadCount = 20;

        Collection<Callable<GithubUser>> tasks = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            Callable<GithubUser> task = () -> githubUserService.getGithubUser(githubUser.login());
            tasks.add(task);
        }
        executorService.invokeAll(tasks);
        await().atMost(5, TimeUnit.SECONDS).until(() -> {
            List<RequestCounter> requestCounterList = requestCounterRepository.findAll();
            return !requestCounterList.isEmpty() && requestCounterList.get(0).getRequestCount() == threadCount;
        });
    }

}
