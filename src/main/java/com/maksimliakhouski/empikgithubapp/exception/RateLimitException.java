package com.maksimliakhouski.empikgithubapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RateLimitException extends RuntimeException {
}
