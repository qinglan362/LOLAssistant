package com.ywh.yxlmzs.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.api.entity.Champion;
import com.ywh.yxlmzs.api.entity.MatchRecord;
import com.ywh.yxlmzs.api.service.GetGameFromGameId;
import com.ywh.yxlmzs.api.service.GetSummoners;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class matchesFromPuuid {

    @Resource
    CallApi callApi;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Resource
    GetSummoners getSummoners;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    GetGameFromGameId getGameFromGameId;

    @GetMapping("/matchesFromPuuid")
    public Object MatchesFromPuuid(@RequestParam Map<String, Object> map) throws JsonProcessingException {
        // 从src/main/resources/static文件夹中中取出champion.json，把其中data的所有对象变成Champion对象，放入List中
        List<Champion> champions = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // 读取JSON文件
            InputStream inputStream = new ClassPathResource("static/champion.json").getInputStream();
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // 获取"data"节点，这是一个包含所有英雄的对象
            JsonNode dataNode = rootNode.get("data");

            // 遍历所有英雄
            dataNode.fieldNames().forEachRemaining(name -> {
                JsonNode championNode = dataNode.get(name);
                Champion champion = new Champion();

                // 设置英雄的名字和ID
                champion.setName(championNode.get("name").asText());
                champion.setChampionId(championNode.get("key").asInt());

                // 将英雄添加到列表中
                champions.add(champion);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response = getSummoners.getSummoners(map);
        JsonNode rootNode = objectMapper.readTree(response);
        String puuId =rootNode.get("puuid").asText();
        String url = "/lol-match-history/v1/products/lol/"+puuId+"/matches";
        String Token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
        String Port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
        JsonNode games= objectMapper.readTree(callApi.callApiGet(url, Token, Port, null)).get("games").get("games");
        List<String> gameIds= new ArrayList<>();
        for (JsonNode game : games) {
            gameIds.add(game.get("gameId").asText());
        }
        List<List<MatchRecord>> gameDetails = new ArrayList<>();
        for (String gameId : gameIds) {
            List<MatchRecord> matchRecords = new ArrayList<>();
            JsonNode gameDetail = objectMapper.readTree(getGameFromGameId.getGameFromGameId(gameId));
            List<JsonNode> participants = Collections.singletonList(gameDetail.get("participants"));
            List<JsonNode> participantIdentities = Collections.singletonList(gameDetail.get("participantIdentities"));
            for (int i = 0; i < participants.size(); i++) {
                for (int j = 0; j < participantIdentities.get(i).size(); j++) {
                    MatchRecord matchRecord = new MatchRecord();
                    matchRecord.setGameName(participantIdentities.get(i).get(j).get("player").get("gameName").asText());
                    matchRecord.setDeaths(participants.get(i).get(j).get("stats").get("deaths").asInt());
                    matchRecord.setKills(participants.get(i).get(j).get("stats").get("kills").asInt());
                    matchRecord.setAssists(participants.get(i).get(j).get("stats").get("assists").asInt());
                    matchRecord.setChampLevel(participants.get(i).get(j).get("stats").get("champLevel").asText());
                    matchRecord.setWin(participants.get(i).get(j).get("stats").get("win").asBoolean());
                    Integer championId = participants.get(i).get(j).get("championId").asInt();
                    champions.stream()
                            .filter(c -> c.getChampionId().equals(championId))
                            .findFirst().ifPresent(champion -> matchRecord.setChampionName(champion.getName()));
                    matchRecords.add(matchRecord);
                }
            }
            gameDetails.add(matchRecords);
        }
         return gameDetails;
    }
}
