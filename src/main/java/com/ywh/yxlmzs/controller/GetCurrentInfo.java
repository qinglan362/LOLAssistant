package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.CurrentInfoB;
import com.ywh.yxlmzs.service.GetGameFromGameId;
import com.ywh.yxlmzs.utils.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GetCurrentInfo {

    @Resource
    CallApi callApi;
    @Resource
    SaveImage saveImage;
    @Resource
    ObjectMapper objectMapper;

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;
    private final AllChampions allChampions;
    private final AllMaps allMaps;
    @Autowired
    public GetCurrentInfo(GetGlobalTokenAndPort getGlobalTokenAndPort, AllChampions allChampions, AllMaps allMaps) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.allChampions = allChampions;
        this.allMaps = allMaps;
    }

    @GetMapping("/getCurrentInfo")
    public CurrentInfoB getCurrentInfo() throws IOException {
        String token = getGlobalTokenAndPort.getToken();
        String port = getGlobalTokenAndPort.getPort();
        JsonNode profileIcons = objectMapper.readTree(callApi.callApiGet("/lol-game-data/assets/v1/profile-icons.json", token, port, null));
        List<String> profileIconList = new ArrayList<>();
        CurrentInfoB currentInfoB=objectMapper.readValue(callApi.callApiGet("/lol-summoner/v1/current-summoner",getGlobalTokenAndPort.getToken(),getGlobalTokenAndPort.getPort(),null),CurrentInfoB.class);
        profileIconList.add(String.valueOf(currentInfoB.getProfileIconId()));
        currentInfoB.setProfileIconId(String.valueOf(saveImage.saveImage(profileIcons,profileIconList,"summonerricon","jpg","profile-icons").get(0).getImage()));
        return currentInfoB;
    }
}
