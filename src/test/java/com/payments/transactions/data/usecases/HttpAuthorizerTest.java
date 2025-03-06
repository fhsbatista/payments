package com.payments.transactions.data.usecases;

import com.payments.transactions.domain.usecases.CreateTransactionInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class HttpAuthorizerTest {
    private RestTemplate restTemplate;

    HttpAuthorizer makeSut() {
        return new HttpAuthorizer(restTemplate);
    }

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
    }

    @Test
    void shouldCallRestTemplateCorrectly() {
        final HttpAuthorizer sut = makeSut();
        final CreateTransactionInput input = mock(CreateTransactionInput.class);

        sut.isAuthorized(input);

        verify(restTemplate).getForEntity(HttpAuthorizer.URL, Map.class);
    }
}