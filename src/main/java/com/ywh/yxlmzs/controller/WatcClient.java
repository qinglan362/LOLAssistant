package com.ywh.yxlmzs.controller;
import com.ywh.yxlmzs.service.WebSocketRegistrationService;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatcClient {
    @Resource
    WebSocketRegistrationService webSocketRegistrationService;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;

    @GetMapping("/StartWebSocketApi")
    public String startWatching(@RequestParam String message) throws Exception {
      //  String subscribeMessage = "[5, \"OnJsonApiEvent_lol-lobby_v2_lobby\"]";
        System.out.println("subscribeMessage: " + message);
        webSocketRegistrationService.getClientWebSocket().subscribe(message);
      return "Started watching " + message;
    }
    @GetMapping("/StopWebSocketApi")
    public String stopWatching (@RequestParam String message) throws Exception {
        System.out.println("unsubscribeMessage: " + message);
    //    String unsubscribeMessage = "[6, \"OnJsonApiEvent_lol-lobby_v2_lobby\"]";
        try {
            webSocketRegistrationService.getClientWebSocket().unsubscribe(message);
            return "Stopped watching " + message;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
}
