package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.Champion;
import com.ywh.yxlmzs.entity.CurrentHistory;
import com.ywh.yxlmzs.entity.MatchRecord;
import com.ywh.yxlmzs.service.GetGameFromGameId;
import com.ywh.yxlmzs.utils.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class GetEnemy {

    @Resource
    ObjectMapper objectMapper;
    @Resource
    CallApi callApi;
    @Resource
    SaveImage saveImage;
    @Resource
    GetGameFromGameId getGameFromGameId;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;
    private  AllChampions allChampions;
    private  AllMaps allMaps;

    @Autowired
    public GetEnemy(GetGlobalTokenAndPort getGlobalTokenAndPort, AllChampions allChampions, AllMaps allMaps) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.allChampions = allChampions;
        this.allMaps = allMaps;
    }

    @GetMapping("/getEnemy")
    public List<CurrentHistory> getEnemy() throws IOException {
        String port = getGlobalTokenAndPort.getPort();
        String token = getGlobalTokenAndPort.getToken();

        JsonNode gameData=objectMapper.readTree(
                callApi.callApiGet(
                        "/lol-gameflow/v1/session",
                        token,
                        port,
                        null
                )
        ).get("gameData");

        System.out.println(gameData);

        JsonNode enemyTea=null;
        String myPuuId=objectMapper.readTree(
                callApi.callApiGet(
                        "/lol-summoner/v1/current-summoner",
                        token,
                        port,
                        null
                )
        ).get("puuid").asText();

        for (JsonNode team:gameData.get("teamOne")){
             if (team.get("puuid").asText().equals(myPuuId)){
                 enemyTea=gameData.get("teamTwo");
                 break;
             }
             enemyTea=gameData.get("teamOne");
        }

        if (gameData.get("queue").get("gameMode").asText().equals("CHERRY")){
            enemyTea=gameData.get("teamOne");
        }

        //取出敌方队伍的puuid
        List<String> enemyPuuIds=new ArrayList<>();
        if (enemyTea != null) {
            for (JsonNode enemy:enemyTea){
                enemyPuuIds.add(enemy.get("puuid").asText());
            }
        }

        System.out.println(enemyPuuIds);

        List<Champion> champions = allChampions.getList();

        List<CurrentHistory> AllMyTeamMatchRecords = new ArrayList<>();

        JsonNode championSummary = objectMapper.readTree(callApi.callApiGet("/lol-game-data/assets/v1/champion-summary.json", token, port, null));
        JsonNode profileIcons = objectMapper.readTree(callApi.callApiGet("/lol-game-data/assets/v1/profile-icons.json", token, port, null));


        for (String puuid : enemyPuuIds) {

            CurrentHistory currentHistory = new CurrentHistory();
            JsonNode current=objectMapper.readTree(callApi.callApiGet("/lol-summoner/v2/summoners/puuid/"+puuid,token,port,null));
            currentHistory.setName(current.get("gameName").asText()+"#"+current.get("tagLine").asText());
            List<String> summonerriconids = new ArrayList<>();
            summonerriconids.add(String.valueOf(current.get("profileIconId").asInt()));
            currentHistory.setIcon(saveImage.saveImage(profileIcons,summonerriconids,"summonerricon","jpg","profile-icons").get(0));

            String tier=objectMapper.readTree(callApi.callApiGet(
                    "/lol-ranked/v1/ranked-stats/"+puuid,
                    token,
                    port,
                    null
            )).get("queueMap").get("RANKED_SOLO_5x5").get("tier").asText();
            if (tier.isEmpty()) {
                tier="unranked";
            }
            //斗魂竞技场分数
            JsonNode rankNode = objectMapper.readTree(callApi.callApiGet("/lol-ranked/v1/ranked-stats/" + puuid, getGlobalTokenAndPort.getToken(), getGlobalTokenAndPort.getPort(), null)).get("queueMap");
            currentHistory.setRate(rankNode.get("CHERRY").get("ratedRating").asText());


            currentHistory.setRank("rank/"+tier.toLowerCase()+".png");

            List<MatchRecord> OneMyTeamPlayerMatchRecords = new ArrayList<>();
            String matchUrl = "/lol-match-history/v1/products/lol/" + puuid + "/matches";
            Map<String, Object> params = new HashMap<>();
            params.put("begIndex", "0");
            params.put("endIndex", "9");
            JsonNode games = objectMapper.readTree(callApi.callApiGet(matchUrl, token, port, params)).get("games").get("games");

            List<String> gameIds= new ArrayList<>();
            Map<String, String> gameDate = new HashMap<>();
            Map<String,String> gameMode=new HashMap<>();
            Map<String,String> gameDeatils=getMapNameById();
            for (JsonNode game : games) {
                gameIds.add(game.get("gameId").asText());
                gameDate.put(game.get("gameId").asText(),formateDate(game.get("gameCreationDate").asText()));
                gameMode.put(game.get("gameId").asText(),gameDeatils.get(game.get("queueId").asText()));
            }
            for (String gameId : gameIds) {
                JsonNode gameDetail = objectMapper.readTree(getGameFromGameId.getGameFromGameId(gameId));
                List<JsonNode> participants = Collections.singletonList(gameDetail.get("participants"));
                List<JsonNode> participantIdentities = Collections.singletonList(gameDetail.get("participantIdentities"));
                for (int i = 0; i < participants.size(); i++) {
                    for (int j = 0; j < participantIdentities.get(i).size(); j++) {
                        if (participantIdentities.get(i).get(j).get("player").get("puuid").asText().equals(puuid)) {
                            MatchRecord matchRecord = new MatchRecord();
                            List<String>  championSummaryIds = new ArrayList<>();
                            championSummaryIds.add(String.valueOf(participants.get(i).get(j).get("championId").asInt()));
                            matchRecord.setChampionIcon(
                                    saveImage.saveImage(championSummary,championSummaryIds,"championSummary",
                                            "jpg",
                                            "champion-summary").get(0));

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
                                    .filter(c -> c.getKey().equals(championId))
                                    .findFirst().ifPresent(champion -> matchRecord.setChampionName(champion.getName()));
                            OneMyTeamPlayerMatchRecords.add(matchRecord);
                        }
                    }
                }
            }
            currentHistory.setMatchRecords(OneMyTeamPlayerMatchRecords);
            AllMyTeamMatchRecords.add(currentHistory);
        }
        return AllMyTeamMatchRecords;

    }

    public String formateDate(String isoDate){
        Instant instant = Instant.parse(isoDate);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        //只显示年月日

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = zonedDateTime.format(formatter);
        return formattedDate;
    }

    public Map<String,String>  getMapNameById() {
        return getStringStringMap(allMaps);
    }

    static Map<String, String> getStringStringMap(AllMaps allMaps) {
        Map<String, String> maps = new HashMap<>();

        for (int i = 0; i< allMaps.getList().size(); i++){
            String id= allMaps.getList().get(i).getId();
            String name= allMaps.getList().get(i).getName();
            maps.put(id,name);
        }
        return maps;
    }
}
