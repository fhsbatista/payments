package com.payments.transactions.data.usecases;

import com.payments.transactions.domain.usecases.SendNotification;
import com.payments.transactions.domain.usecases.SendNotificationInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

record RequestModel(String email, String message) {

}

public class HttpSendNotification implements SendNotification {
    public final static String URL = "https://util.devi.tools/api/v1/notify";
    private RestTemplate restTemplate;

    public HttpSendNotification(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean send(SendNotificationInput input) {
        final RequestModel params = new RequestModel(input.email(), input.message());
        final ResponseEntity result = restTemplate.postForEntity(URL, params, String.class);

        return result.getStatusCode().is2xxSuccessful();
    }
}
