package com.xy.learning.week1.web;

import com.xy.learning.week1.config.AppProperties;
import com.xy.learning.week1.notify.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Week1Controller {

    private final AppProperties appProperties;
    private final NotificationService notificationService;

    public Week1Controller(AppProperties appProperties, NotificationService notificationService) {
        this.appProperties = appProperties;
        this.notificationService = notificationService;
    }

    @GetMapping("/config")
    public Map<String,Object> config(){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("appName",appProperties.getName());
        result.put("timeout",appProperties.getTimeoutMs());
        result.put("whitelist",appProperties.getWhitelist());
        result.put("notifyChannelImpl",notificationService.getClass().getSimpleName());
        return result;
    }

    @PostMapping("/notify")
    public Map<String,Object> notify(@RequestBody String message){
        return Map.of("result",notificationService.send(message));
    }
}
