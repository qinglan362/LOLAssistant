package com.ywh.yxlmzs.controller;
import com.ywh.yxlmzs.service.WebSocketRegistrationService;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatcClient {
    @Autowired
    private WebSocketRegistrationService webSocketRegistrationService;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;

    @GetMapping("/start-accept-game")
    public String startWatching() throws Exception {
        String  port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
        String  token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
        String url = "wss://127.0.0.1:"+port+"/";
        webSocketRegistrationService.registerWebSocketHandler(url, "riot", token);
        return "Started watching " + url;
    }
}
