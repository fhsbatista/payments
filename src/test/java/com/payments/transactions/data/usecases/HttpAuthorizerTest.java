package com.payments.transactions.data.usecases;

import com.payments.transactions.domain.usecases.CreateTransactionInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HttpAuthorizerTest {
    private RestTemplate restTemplate;

    HttpAuthorizer makeSut() {
        return new HttpAuthorizer(restTemplate);
    }

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        mockSuccess();
    }

    void mockSuccess() {
        final ResponseModel response = new ResponseModel("success", new ResponseModel.Data(true)
        );
        when(restTemplate.getForEntity(anyString(), eq(ResponseModel.class)))
                .thenReturn(ResponseEntity.ok(response));
    }

    @Test
    void shouldCallRestTemplateCorrectly() {
        final HttpAuthorizer sut = makeSut();
        final CreateTransactionInput input = mock(CreateTransactionInput.class);

        sut.isAuthorized(input);

        verify(restTemplate).getForEntity(HttpAuthorizer.URL, ResponseModel.class);
    }

    @Test
    void shouldReturnFalseIfResponseReturnsFalse() {
        final HttpAuthorizer sut = makeSut();
        final CreateTransactionInput input = mock(CreateTransactionInput.class);
        final ResponseModel response = new ResponseModel("success", new ResponseModel.Data(false)
        );
        when(restTemplate.getForEntity(anyString(), eq(ResponseModel.class)))
                .thenReturn(ResponseEntity.ok(response));

        final boolean result = sut.isAuthorized(input);

        assertFalse(result);
    }

    @Test
    void shouldReturnTrueIfResponseReturnsTrue() {
        final HttpAuthorizer sut = makeSut();
        final CreateTransactionInput input = mock(CreateTransactionInput.class);

        final boolean result = sut.isAuthorized(input);

        assertTrue(result);
    }
}