package com.payments.transactions.domain.usecases;

public interface SendNotification {
    boolean send(SendNotificationInput input);
}
