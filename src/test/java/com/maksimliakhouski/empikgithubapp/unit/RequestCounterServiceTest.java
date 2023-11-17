package com.maksimliakhouski.empikgithubapp.unit;

import com.maksimliakhouski.empikgithubapp.model.RequestCounter;
import com.maksimliakhouski.empikgithubapp.repository.RequestCounterRepository;
import com.maksimliakhouski.empikgithubapp.service.RequestCounterService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class RequestCounterServiceTest {

    private final RequestCounterRepository requestCounterRepository = mock(RequestCounterRepository.class);
    private final RequestCounterService requestCounterService = new RequestCounterService(requestCounterRepository);

    @Test
    public void testCreateRequestCounter() {
        String login = "not existing login";

        when(requestCounterRepository.existsByLogin(any())).thenReturn(true);
        when(requestCounterRepository.existsByLogin(login)).thenReturn(false);

        requestCounterService.incrementRequestCount("existing login");
        requestCounterService.incrementRequestCount(login);

        verify(requestCounterRepository, times(1)).save(any(RequestCounter.class));
        verify(requestCounterRepository, times(2)).incrementRequestCount(anyString());
    }

}
