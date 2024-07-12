package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.Champion;
import com.ywh.yxlmzs.entity.MatchRecord;
import com.ywh.yxlmzs.service.GetGameFromGameId;
import com.ywh.yxlmzs.service.GetSummoners;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class GetCurrentGameSummonersId {

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

    @GetMapping("/getCurrentGameSummonersId")
    public Object getCurrentGameSummonersId() throws IOException {

        List<String> puuids=new ArrayList<>();

        String url="/lol-champ-select/v1/session";
        String port=getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
        String token=getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
        String result=callApi.callApiGet(url,token,port,null);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(result);
        JsonNode myTeam=rootNode.get("myTeam");
        if (myTeam != null && myTeam.isArray()) {
            for (JsonNode summonerNode : myTeam) {
                JsonNode summonerIdNode = summonerNode.get("puuid");
                if (summonerIdNode != null) {
                    puuids.add(summonerIdNode.asText());
                }
            }
        }

        List<Champion> champions = new ArrayList<>();
        try {
            InputStream inputStream = new ClassPathResource("static/champion.json").getInputStream();
            JsonNode rootNode1 = objectMapper.readTree(inputStream);
            JsonNode dataNode = rootNode1.get("data");
            dataNode.fieldNames().forEachRemaining(name -> {
                JsonNode championNode = dataNode.get(name);
                Champion champion = new Champion();
                champion.setName(championNode.get("name").asText());
                champion.setChampionId(championNode.get("key").asInt());
                champions.add(champion);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<List<MatchRecord>> AllMyTeamMatchRecords = new ArrayList<>();

        for (String puuid : puuids) {
            List<MatchRecord> OneMyTeamPlayerMatchRecords = new ArrayList<>();
            String matchUrl = "/lol-match-history/v1/products/lol/" + puuid + "/matches";
            String Token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
            String Port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
            Map<String, Object> params = new HashMap<>();
            params.put("begIndex", "0");
            params.put("endIndex", "4");
            JsonNode games = objectMapper.readTree(callApi.callApiGet(matchUrl, Token, Port, params)).get("games").get("games");

            List<String> gameIds= new ArrayList<>();
            Map<String, String> gameDate = new HashMap<>();
            Map<String,String> gameMode=new HashMap<>();
            Map<String,String> gameDeatils=getMapNameById();
            for (JsonNode game : games) {
                if(Objects.equals(game.get("mapId").asText(), "11") || Objects.equals(game.get("mapId").asText(), "12")) {
                    gameIds.add(game.get("gameId").asText());
                    gameDate.put(game.get("gameId").asText(),formateDate(game.get("gameCreationDate").asText()));
                    gameMode.put(game.get("gameId").asText(),gameDeatils.get(game.get("queueId").asText()));
                }
            }
            for (String gameId : gameIds) {
                JsonNode gameDetail = objectMapper.readTree(getGameFromGameId.getGameFromGameId(gameId));
                List<JsonNode> participants = Collections.singletonList(gameDetail.get("participants"));
                List<JsonNode> participantIdentities = Collections.singletonList(gameDetail.get("participantIdentities"));
                for (int i = 0; i < participants.size(); i++) {
                    for (int j = 0; j < participantIdentities.get(i).size(); j++) {
                        if (participantIdentities.get(i).get(j).get("player").get("puuid").asText().equals(puuid)) {
                            MatchRecord matchRecord = new MatchRecord();
                            matchRecord.setGameName(participantIdentities.get(i).get(j).get("player").get("gameName").asText()+"#"+participantIdentities.get(i).get(j).get("player").get("tagLine").asText());
                            matchRecord.setDeaths(participants.get(i).get(j).get("stats").get("deaths").asInt());
                            matchRecord.setKills(participants.get(i).get(j).get("stats").get("kills").asInt());
                            matchRecord.setAssists(participants.get(i).get(j).get("stats").get("assists").asInt());
                            matchRecord.setChampLevel(participants.get(i).get(j).get("stats").get("champLevel").asText());
                            matchRecord.setWin(participants.get(i).get(j).get("stats").get("win").asBoolean());
                            matchRecord.setGameId(gameId);
                            matchRecord.setDate(gameDate.get(gameId));
                            matchRecord.setMapName(gameMode.get(gameId));
                            Integer championId = participants.get(i).get(j).get("championId").asInt();
                            champions.stream()
                                    .filter(c -> c.getChampionId().equals(championId))
                                    .findFirst().ifPresent(champion -> matchRecord.setChampionName(champion.getName()));
                            OneMyTeamPlayerMatchRecords.add(matchRecord);
                        }
                    }
                }
            }
            AllMyTeamMatchRecords.add(OneMyTeamPlayerMatchRecords);
        }
        return AllMyTeamMatchRecords;
    }

    public String formateDate(String isoDate){
        Instant instant = Instant.parse(isoDate);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = zonedDateTime.format(formatter);
        return formattedDate;
    }

    public Map<String,String>  getMapNameById() {
        Map<String, String> maps = new HashMap<>();
        try {
            InputStream inputStream = new ClassPathResource("static/maps.json").getInputStream();
            JsonNode rootNode = objectMapper.readTree(inputStream);
            for (JsonNode node : rootNode) {
                String id = node.get("id").asText();
                String name = node.get("name").asText();
                maps.put(id, name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return maps;
    }

}