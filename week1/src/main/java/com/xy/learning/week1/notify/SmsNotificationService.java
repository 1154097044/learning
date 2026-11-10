package com.xy.learning.week1.notify;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


/**
 * 短信实现：仅当 app.notify-channel=sms 时装配。
 * matchIfMissing = true 表示配置缺省时也用短信渠道。
 */
@Service
@ConditionalOnProperty(name = "app.notify-channel",havingValue = "sms")
public class SmsNotificationService implements NotificationService{
    @Override
    public String send(String message) {
        return "[SMS] " + message;
    }
}
