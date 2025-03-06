package com.payments.transactions.data.usecases;

import com.payments.transactions.domain.usecases.Authorizer;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import org.springframework.web.client.RestTemplate;

public class HttpAuthorizer implements Authorizer {
    private RestTemplate restTemplate;

    public HttpAuthorizer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isAuthorized(CreateTransactionInput input) {
        return false;
    }
}
