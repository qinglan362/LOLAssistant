package com.ywh.yxlmzs.controller;

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
public class GetCurrentGameSummonersId {

    @Resource
    CallApi callApi;
    @Resource
    GetGameFromGameId getGameFromGameId;
    @Resource
    SaveImage saveImage;

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;
    private final AllChampions allChampions;
    private final AllMaps allMaps;
    @Autowired
    public GetCurrentGameSummonersId(GetGlobalTokenAndPort getGlobalTokenAndPort, AllChampions allChampions, AllMaps allMaps) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.allChampions = allChampions;
        this.allMaps = allMaps;
    }
    @GetMapping("/getCurrentGameSummonersId")
    public List<CurrentHistory> getCurrentGameSummonersId() throws IOException {
        List<String> puuids=new ArrayList<>();
        String url="/lol-champ-select/v1/session";
        String port=getGlobalTokenAndPort.getPort();
        String token=getGlobalTokenAndPort.getToken();
        String result=callApi.callApiGet(url,token,port,null);
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(result);

        JsonNode myTeam=rootNode.get("myTeam");

       String myselfPuuid=objectMapper.readTree(callApi.callApiGet("/lol-summoner/v1/current-summoner",token,port,null)).get("puuid").asText();

        if (myTeam.isArray()) {
            for (JsonNode summonerNode : myTeam) {
                if (!Objects.equals(summonerNode.get("puuid").asText(), "")) {
                    if (!Objects.equals(summonerNode.get("puuid").asText(), myselfPuuid)){
                        puuids.add(summonerNode.get("puuid").asText());
                    }
                }
            }
        }

        List<Champion> champions = allChampions.getList();

        List<CurrentHistory> AllMyTeamMatchRecords = new ArrayList<>();

        for (String puuid : puuids) {

            CurrentHistory currentHistory = new CurrentHistory();
            JsonNode current=objectMapper.readTree(callApi.callApiGet("/lol-summoner/v2/summoners/puuid/"+puuid,token,port,null));
            currentHistory.setName(current.get("gameName").asText()+"#"+current.get("tagLine").asText());
            currentHistory.setIcon(saveImage.saveImage("summonerricon",current.get("profileIconId").asText(),"jpg","profile-icons"));

            String tier=objectMapper.readTree(callApi.callApiGet(
                    "/lol-ranked/v1/ranked-stats/"+puuid,
                    token,
                    port,
                    null
            )).get("queueMap").get("RANKED_SOLO_5x5").get("tier").asText();
            if (tier.isEmpty()) {
                 tier="unranked";
            }
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
                            matchRecord.setChampionIcon(
                                    saveImage.saveImage("championSummary",
                                             participants.get(i).get(j).get("championId").asText(),
                                            "png",
                                            "champion-summary")
                            );
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