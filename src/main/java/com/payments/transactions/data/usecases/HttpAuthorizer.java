package com.payments.transactions.data.usecases;

import com.payments.transactions.domain.usecases.Authorizer;
import com.payments.transactions.domain.usecases.CreateTransactionInput;
import org.springframework.web.client.RestTemplate;

record ResponseModel(String status, Data data) {
    record Data(boolean authorization) {
    }
}

public class HttpAuthorizer implements Authorizer {
    public final static String URL = "https://util.devi.tools/api/v2/authorize";
    private RestTemplate restTemplate;

    public HttpAuthorizer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean isAuthorized(CreateTransactionInput input) {
        final var result = restTemplate.getForEntity(URL, ResponseModel.class);

        return result.getBody().data().authorization();
    }
}
