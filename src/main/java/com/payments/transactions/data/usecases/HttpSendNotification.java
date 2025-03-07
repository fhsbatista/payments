package com.payments.transactions.data.usecases;

import com.payments.transactions.domain.usecases.SendNotification;
import com.payments.transactions.domain.usecases.SendNotificationInput;

public class HttpSendNotification implements SendNotification {
    @Override
    public boolean send(SendNotificationInput input) {
        return false;
    }
}
