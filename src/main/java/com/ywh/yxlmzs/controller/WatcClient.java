package com.ywh.yxlmzs.controller;
import com.ywh.yxlmzs.service.WebSocketRegistrationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatcClient {

    @Resource
    WebSocketRegistrationService webSocketRegistrationService;

    @GetMapping("/StartWebSocketApi")
    public String startWatching(@RequestParam String message) throws Exception {
            webSocketRegistrationService.getClientWebSocket().subscribe(message);
         return "Started watching " + message;
    }

    @GetMapping("/StopWebSocketApi")
    public String stopWatching(@RequestParam String message) throws Exception {
        try {
                webSocketRegistrationService.getClientWebSocket().unsubscribe(message);
                return "Stopped watching " + message;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
