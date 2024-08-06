package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;
    private GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public spectateLaunch(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }
    @PostMapping("/spectateLaunch")
    public String spectateLaunch(@RequestParam  String name) throws JsonProcessingException {
        //待修改
        System.out.println(objectMapper.readTree(callApi.callApiGet(
                "/lol-summoner/v1/summoners",
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                Map.of("name",name)
        )).toPrettyString());

        Map<String,Object> params=Map.of("summonerId","summonerId");

        System.out.println(objectMapper.readTree( callApi.callApiPost(
                "/lol-spectator/v1/spectate/launch",
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                params
        )).toPrettyString());
        return "success";
    }
}
