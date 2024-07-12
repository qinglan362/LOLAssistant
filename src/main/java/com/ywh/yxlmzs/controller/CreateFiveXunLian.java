package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class CreateFiveXunLian {

    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Resource
    CallApi callApi;


   @PostMapping("/createFiveXunLian")
   public String createFiveXunLian() throws IOException {
    String url = "/lol-lobby/v2/lobby";
    String token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
    String port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
       System.out.println(11);
    Map<String, Object> map = Map.of(
            "customGameLobby", Map.of(
                    "configuration", Map.of(
                            "gameMode", "PRACTICETOOL",
                            "gameMutator", "",
                            "gameServerRegion", "",
                            "mapId", 11,
                            "mutators", Map.of("id", 1),
                            "spectatorPolicy", "AllAllowed",
                            "teamSize", 5
                    ),
                    "lobbyName", "PRACTICETOOL",
                    "lobbyPassword", ""
            ),
            "isCustom", true
    );

    return callApi.callApiPost(url, token, port, map);
}
}
