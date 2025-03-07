package com.payments.transactions.domain.usecases;

import com.payments.users.domain.entities.User;

public record SendNotificationInput(String email, String message) {
    public static SendNotificationInput fromUser(User user, String message) {
        return new SendNotificationInput(user.email(), message);
    }
}
