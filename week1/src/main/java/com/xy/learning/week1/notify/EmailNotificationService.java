package com.xy.learning.week1.notify;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.notify-channel",havingValue = "email")
public class EmailNotificationService implements NotificationService{
    @Override
    public String send(String message) {
        return "[EMAIL] " + message;
    }
}
