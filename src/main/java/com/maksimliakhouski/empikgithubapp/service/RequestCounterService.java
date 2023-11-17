package com.maksimliakhouski.empikgithubapp.service;

import com.maksimliakhouski.empikgithubapp.model.RequestCounter;
import com.maksimliakhouski.empikgithubapp.repository.RequestCounterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RequestCounterService {

    private final Logger logger = LoggerFactory.getLogger(RequestCounterService.class);
    private final RequestCounterRepository requestCounterRepository;

    public RequestCounterService(RequestCounterRepository requestCounterRepository) {
        this.requestCounterRepository = requestCounterRepository;
    }

    public void incrementRequestCount(String login) {
        synchronized(this) {
            if (!requestCounterRepository.existsByLogin(login)) {
                logger.info("Creating request counter with login: " + login);
                createRequestCounter(login);
            }
        }
        logger.info("Incrementing request count of request counter with login: " + login);
        requestCounterRepository.incrementRequestCount(login);
    }

    private void createRequestCounter(String login) {
        RequestCounter requestCounter = new RequestCounter();
        requestCounter.setLogin(login);
        requestCounter.setRequestCount(0);
        requestCounterRepository.save(requestCounter);
    }
}
