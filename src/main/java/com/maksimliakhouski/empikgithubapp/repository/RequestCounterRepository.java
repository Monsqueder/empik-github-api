package com.maksimliakhouski.empikgithubapp.repository;

import com.maksimliakhouski.empikgithubapp.model.RequestCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RequestCounterRepository extends JpaRepository<RequestCounter, Long> {

    boolean existsByLogin(String login);

    @Modifying
    @Transactional
    @Query("update RequestCounter rc set rc.requestCount = rc.requestCount + 1 WHERE rc.login = :login")
    void incrementRequestCount(String login);
}
