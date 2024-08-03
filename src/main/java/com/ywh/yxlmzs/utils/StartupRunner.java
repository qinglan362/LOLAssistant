package com.ywh.yxlmzs.utils;

import com.ywh.yxlmzs.service.GetChampions;
import com.ywh.yxlmzs.service.GetVersion;
import com.ywh.yxlmzs.service.WebSocketRegistrationService;
import jakarta.annotation.Resource;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

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

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;
    private final Position position;
    @Autowired
    public StartupRunner(GetGlobalTokenAndPort getGlobalTokenAndPort, Position position ) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.position = position;
    }

    @Override
    public void run(String... args) throws Exception {
        Unirest.config().verifySsl(false);
        getTokenAndPort.getPortAndToken();
        getMaps.getMaps();
        getVersion.getVersions();
        getChampions.getChampions();
        startWebsocket();
        setPositions();
      }

    public void startWebsocket() throws Exception {
        String  port = getGlobalTokenAndPort.getPort();
        String  token = getGlobalTokenAndPort.getToken();
        String url = "wss://127.0.0.1:"+port+"/";
        webSocketRegistrationService.registerWebSocketHandler(url, "riot", token);
       }
    public void setPositions(){
        Map<String, String> position = new HashMap<>();
        position.put("IRON", "黑铁");
        position.put("BRONZE", "青铜");
        position.put("SILVER", "白银");
        position.put("GOLD", "黄金");
        position.put("PLATINUM", "铂金");
        position.put("EMERALD", "翡翠");
        position.put("DIAMOND", "钻石");
        position.put("MASTER", "大师");
        position.put("GRANDMASTER", "宗师");
        position.put("CHALLENGER", "最强王者");
        position.put("NA", "无段位");
        position.put("", "无段位");
        this.position.setPosition(position);
    }

}
