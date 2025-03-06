package com.payments.transactions.data.usecases;

import com.payments.transactions.domain.usecases.Authorizer;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class HttpAuthorizer implements Authorizer {
    public final static String URL = "https://util.devi.tools/api/v2/authorize";
    private RestTemplate restTemplate;

    public HttpAuthorizer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isAuthorized(CreateTransactionInput input) {
        restTemplate.getForEntity(URL, Map.class);
        return false;
    }
}
