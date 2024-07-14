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
    WebSocketRegistrationService webSocketRegistrationService;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public StartupRunner(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @Override
    public void run(String... args) throws Exception {
        Unirest.config().verifySsl(false);
        getTokenAndPort.getPortAndToken();
        getMaps.getMaps();
        getVersion.getVersions();
        getChampions.getChampions();
        startWebsocket();
    }

    public void startWebsocket() throws Exception {
        String  port = getGlobalTokenAndPort.getPort();
        String  token = getGlobalTokenAndPort.getToken();
        String url = "wss://127.0.0.1:"+port+"/";
        webSocketRegistrationService.registerWebSocketHandler(url, "riot", token);
    }
}
