package com.ywh.yxlmzs.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.service.GetSummoners;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class spectateLaunch {
    @Resource
    GetSummoners getSummoners;
    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public spectateLaunch(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }
    @PostMapping("/spectateLaunch")
    public String SpectateLaunch(@RequestParam  Map<String,String> map) throws JsonProcessingException {
        JSONObject jsonObject = JSONObject.parseObject(getSummoners.getSummoners(Map.of("name",map.get("name"))));
        String puuId = jsonObject.getString("puuid");
        Map<String, Object> data = Map.of(
                "allowObserveMode", "ALL",
                "dropInSpectateGameId", map.get("name"),
                "gameQueueType", "",
                "puuid", puuId
        );
        System.out.println(objectMapper.readTree(callApi.callApiPost("/lol-spectator/v1/spectate/launch",
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                data)).toPrettyString());
        return "success";
    }
}
