package com.maksimliakhouski.empikgithubapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "REQUEST_COUNTER")
public class RequestCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "LOGIN", unique = true)
    private String login;

    @Column(name = "REQUEST_COUNT")
    private int requestCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }
}
