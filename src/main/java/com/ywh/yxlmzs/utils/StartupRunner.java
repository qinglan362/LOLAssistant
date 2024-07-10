package com.ywh.yxlmzs.utils;

import com.ywh.yxlmzs.service.GetChampions;
import com.ywh.yxlmzs.service.GetVersion;
import com.ywh.yxlmzs.service.WebSocketRegistrationService;
import jakarta.annotation.Resource;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    @Resource
    GetTokenAndPort getTokenAndPort;
    @Resource
    GetMaps getMaps;
    @Resource
    GetVersion getVersion;
    @Resource
    GetChampions getChampions;
    @Resource
    GetEventHelp getEventHelp;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Resource
    WebSocketRegistrationService webSocketRegistrationService;


    @Override
    public void run(String... args) throws Exception {
        Unirest.config().verifySsl(false);
        getTokenAndPort.getPortAndToken();
        getMaps.getMaps();
        getVersion.getVersions();
        getChampions.getChampions();
        getEventHelp.getEventHelp();
        startWebsocket();
    }

    public void startWebsocket() throws Exception {
        String  port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
        String  token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
        String url = "wss://127.0.0.1:"+port+"/";
        webSocketRegistrationService.registerWebSocketHandler(url, "riot", token);
    }
}
