package com.payments.transactions.data.usecases;

import com.payments.transactions.domain.usecases.SendNotificationInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HttpSendNotificationTest {
    private RestTemplate restTemplate;

    HttpSendNotification makeSut() {
        return new HttpSendNotification(restTemplate);
    }

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        mockSuccess();
    }

    void mockSuccess() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(""));
    }

    @Test
    void shouldCallRestTemplateCorrectly() {
        final HttpSendNotification sut = makeSut();
        final SendNotificationInput input = mock(SendNotificationInput.class);

        sut.send(input);

        final RequestModel expectedParams = new RequestModel(input.email(), input.message());
        verify(restTemplate).postForEntity(HttpSendNotification.URL, expectedParams, String.class);
    }

    @Test
    void shouldReturnFalseIfRestTemplateNotReturn2xx() {
        final HttpSendNotification sut = makeSut();
        final SendNotificationInput input = mock(SendNotificationInput.class);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        final boolean result = sut.send(input);

        assertFalse(result);
    }

    @Test
    void shouldReturnTrueIfRestTemplateReturn2xx() {
        final HttpSendNotification sut = makeSut();
        final SendNotificationInput input = mock(SendNotificationInput.class);

        final boolean result = sut.send(input);

        assertTrue(result);
    }
}