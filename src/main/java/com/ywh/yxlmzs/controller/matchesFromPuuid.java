package com.ywh.yxlmzs.controller;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ywh.yxlmzs.entity.Champion;
//import com.ywh.yxlmzs.entity.MatchRecord;
//import com.ywh.yxlmzs.entity.Rank;
//import com.ywh.yxlmzs.service.GetGameFromGameId;
//import com.ywh.yxlmzs.service.GetSummoners;
//import com.ywh.yxlmzs.utils.*;
//import jakarta.annotation.Resource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import java.io.IOException;
//import java.time.Instant;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@RestController
//public class matchesFromPuuid {
//
//    @Resource
//    CallApi callApi;
//    @Resource
//    GetSummoners getSummoners;
//    @Resource
//    ObjectMapper objectMapper;
//    @Resource
//    GetGameFromGameId getGameFromGameId;
//
//    private GetGlobalTokenAndPort getGlobalTokenAndPort;
//    private AllChampions allChampions;
//    private AllMaps allMaps;
//    private Position position;
//    @Autowired
//    public matchesFromPuuid(GetGlobalTokenAndPort getGlobalTokenAndPort, AllChampions allChampions, AllMaps allMaps,Position position) {
//        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
//        this.allChampions = allChampions;
//        this.allMaps = allMaps;
//        this.position = position;
//    }
//
//    @GetMapping("/matchesFromPuuid")
//    public Object MatchesFromPuuid(@RequestParam Map<String, Object> map) throws IOException {
//        List<Champion> champions = allChampions.getList();
//        String response = getSummoners.getSummoners(map);
//        JsonNode rootNode = objectMapper.readTree(response);
//        String puuId =rootNode.get("puuid").asText();
//        String url = "/lol-match-history/v1/products/lol/"+puuId+"/matches";
//        String Token = getGlobalTokenAndPort.getToken();
//        String Port = getGlobalTokenAndPort.getPort();
//
//        JsonNode games= objectMapper.readTree(callApi.callApiGet(url, Token, Port, null)).get("games").get("games");
//        List<String> gameIds= new ArrayList<>();
//        Map<String, String> gameDate = new HashMap<>();
//        Map<String,String> gameMode=new HashMap<>();
//        Map<String,String> gameDeatils=getMapNameById();
//        for (JsonNode game : games) {
//            if(Objects.equals(game.get("mapId").asText(), "11") || Objects.equals(game.get("mapId").asText(), "12")) {
//                gameIds.add(game.get("gameId").asText());
//                gameDate.put(game.get("gameId").asText(),formateDate(game.get("gameCreationDate").asText()));
//                gameMode.put(game.get("gameId").asText(),gameDeatils.get(game.get("queueId").asText()));
//            }
//        }
//        List<List<MatchRecord>> gameDetails = new ArrayList<>();
//        for (String gameId : gameIds) {
//            List<MatchRecord> matchRecords = new ArrayList<>();
//            JsonNode gameDetail = objectMapper.readTree(getGameFromGameId.getGameFromGameId(gameId));
//            List<JsonNode> participants = Collections.singletonList(gameDetail.get("participants"));
//            List<JsonNode> participantIdentities = Collections.singletonList(gameDetail.get("participantIdentities"));
//            for (int i = 0; i < participants.size(); i++) {
//                for (int j = 0; j < participantIdentities.get(i).size(); j++) {
//                    MatchRecord matchRecord = new MatchRecord();
//                    Rank rank=rank(participantIdentities.get(i).get(j).get("player").get("puuid").asText());
//                    matchRecord.setRank(rank);
//                    matchRecord.setGameName(participantIdentities.get(i).get(j).get("player").get("gameName").asText()+"#"+participantIdentities.get(i).get(j).get("player").get("tagLine").asText());
//                    matchRecord.setDeaths(participants.get(i).get(j).get("stats").get("deaths").asInt());
//                    matchRecord.setKills(participants.get(i).get(j).get("stats").get("kills").asInt());
//                    matchRecord.setAssists(participants.get(i).get(j).get("stats").get("assists").asInt());
//                    matchRecord.setChampLevel(participants.get(i).get(j).get("stats").get("champLevel").asText());
//                    matchRecord.setWin(participants.get(i).get(j).get("stats").get("win").asBoolean());
//                    matchRecord.setGameId(gameId);
//                    matchRecord.setDate(gameDate.get(gameId));
//                    matchRecord.setMapName(gameMode.get(gameId));
//                    Integer championId = participants.get(i).get(j).get("championId").asInt();
//                    champions.stream()
//                            .filter(c -> c.getKey().equals(championId))
//                            .findFirst().ifPresent(champion -> matchRecord.setChampionName(champion.getName()));
//                    matchRecords.add(matchRecord);
//                }
//            }
//            gameDetails.add(matchRecords);
//        }
//         return gameDetails;
//    }
//
//    public String formateDate(String isoDate){
//        Instant instant = Instant.parse(isoDate);
//        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String formattedDate = zonedDateTime.format(formatter);
//        return formattedDate;
//    }
//
//    public Map<String,String>  getMapNameById() {
//        Map<String, String> maps = new HashMap<>();
//        for (int i=0;i<allMaps.getList().size();i++){
//              String id= allMaps.getList().get(i).getId();
//               String name= allMaps.getList().get(i).getName();
//                maps.put(id,name);
//        }
//        return maps;
//    }
//    public Rank rank(String puuId) throws JsonProcessingException {
//         JsonNode rankNode= objectMapper.readTree(callApi.callApiGet("/lol-ranked/v1/ranked-stats/"+puuId, getGlobalTokenAndPort.getToken(), getGlobalTokenAndPort.getPort(), null)).get("queueMap");
//         Rank rank=new Rank();
//         rank.setRANKED_FLEX_SR_CurrentSeason_Division(returnDivision(rankNode.get("RANKED_FLEX_SR").get("division").asText()));
//         rank.setRANKED_FLEX_SR_CurrentSeason_HighestDivision(returnDivision(rankNode.get("RANKED_FLEX_SR").get("highestDivision").asText()));
//         rank.setRANKED_FLEX_SR_CurrentSeason_HighestTier(position.getPosition().get(rankNode.get("RANKED_FLEX_SR").get("highestTier").asText()));
//         rank.setRANKED_FLEX_SR_CurrentSeason_LeaguePoints(rankNode.get("RANKED_FLEX_SR").get("leaguePoints").asText());
//         rank.setRANKED_FLEX_SR_PreviousSeasonHighestTier(position.getPosition().get(rankNode.get("RANKED_FLEX_SR").get("previousSeasonHighestTier").asText()));
//         rank.setRANKED_FLEX_SR_CurrentSeason_Tier(position.getPosition().get(rankNode.get("RANKED_FLEX_SR").get("tier").asText()));
//         rank.setRANKED_SOLO_5x5_CurrentSeason_Tier(position.getPosition().get(rankNode.get("RANKED_SOLO_5x5").get("tier").asText()));
//         rank.setRANKED_SOLO_5x5_CurrentSeason_Division(returnDivision(rankNode.get("RANKED_SOLO_5x5").get("division").asText()));
//         rank.setRANKED_SOLO_5x5_CurrentSeason_HighestDivision(returnDivision(rankNode.get("RANKED_SOLO_5x5").get("highestDivision").asText()));
//         rank.setRANKED_SOLO_5x5_CurrentSeason_HighestTier(position.getPosition().get(rankNode.get("RANKED_SOLO_5x5").get("highestTier").asText()));
//         rank.setRANKED_SOLO_5x5_CurrentSeason_LeaguePoints(rankNode.get("RANKED_SOLO_5x5").get("leaguePoints").asText());
//         rank.setRANKED_SOLO_5x5_PreviousSeasonHighestTier(position.getPosition().get(rankNode.get("RANKED_SOLO_5x5").get("previousSeasonHighestTier").asText()));
//         return rank;
//    }
//
//    public String returnDivision(String division){
//        if (division.equals("NA")) {
//            return "";
//        } else {
//            return division;
//        }
//    }
//}
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.Champion;
import com.ywh.yxlmzs.entity.MatchRecord;
import com.ywh.yxlmzs.entity.Rank;
import com.ywh.yxlmzs.service.GetGameFromGameId;
import com.ywh.yxlmzs.service.GetSummoners;
import com.ywh.yxlmzs.utils.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.ywh.yxlmzs.controller.GetCurrentGameSummonersId.getStringStringMap;

@RestController
public class matchesFromPuuid {

    @Resource
    CallApi callApi;
    @Resource
    GetSummoners getSummoners;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    GetGameFromGameId getGameFromGameId;

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;
    private final AllChampions allChampions;
    private final AllMaps allMaps;
    private final Position position;

    @Autowired
    public matchesFromPuuid(GetGlobalTokenAndPort getGlobalTokenAndPort, AllChampions allChampions, AllMaps allMaps, Position position) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.allChampions = allChampions;
        this.allMaps = allMaps;
        this.position = position;
    }

    @GetMapping("/matchesFromPuuid")
    public Object MatchesFromPuuid(@RequestParam Map<String, Object> map) throws IOException, ExecutionException, InterruptedException {
        List<Champion> champions = allChampions.getList();
        String response = getSummoners.getSummoners(map);
        JsonNode rootNode = objectMapper.readTree(response);
        String puuId = rootNode.get("puuid").asText();
        String url = "/lol-match-history/v1/products/lol/" + puuId + "/matches";
        String token = getGlobalTokenAndPort.getToken();
        String port = getGlobalTokenAndPort.getPort();

        JsonNode games = objectMapper.readTree(callApi.callApiGet(url, token, port, null)).get("games").get("games");
        List<String> gameIds = new ArrayList<>();
        Map<String, String> gameDate = new HashMap<>();
        Map<String, String> gameMode = new HashMap<>();
        Map<String, String> gameDetails = getMapNameById();

        for (JsonNode game : games) {
            if (Objects.equals(game.get("mapId").asText(), "11") || Objects.equals(game.get("mapId").asText(), "12")) {
                gameIds.add(game.get("gameId").asText());
                gameDate.put(game.get("gameId").asText(), formatDate(game.get("gameCreationDate").asText()));
                gameMode.put(game.get("gameId").asText(), gameDetails.get(game.get("queueId").asText()));
            }
        }

        List<CompletableFuture<List<MatchRecord>>> futures = gameIds.stream()
                .map(gameId -> CompletableFuture.supplyAsync(() -> fetchMatchRecords(gameId, gameDate, gameMode, champions)))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                .get();
    }

    private List<MatchRecord> fetchMatchRecords(String gameId, Map<String, String> gameDate, Map<String, String> gameMode, List<Champion> champions) {
        List<MatchRecord> matchRecords = new ArrayList<>();
        try {
            JsonNode gameDetail = objectMapper.readTree(getGameFromGameId.getGameFromGameId(gameId));
            JsonNode participants = gameDetail.get("participants");
            JsonNode participantIdentities = gameDetail.get("participantIdentities");

            for (int i = 0; i < participants.size(); i++) {
                MatchRecord matchRecord = new MatchRecord();
                Rank rank = rank(participantIdentities.get(i).get("player").get("puuid").asText());
                matchRecord.setRank(rank);
                matchRecord.setGameName(participantIdentities.get(i).get("player").get("gameName").asText() + "#" + participantIdentities.get(i).get("player").get("tagLine").asText());
                matchRecord.setDeaths(participants.get(i).get("stats").get("deaths").asInt());
                matchRecord.setKills(participants.get(i).get("stats").get("kills").asInt());
                matchRecord.setAssists(participants.get(i).get("stats").get("assists").asInt());
                matchRecord.setChampLevel(participants.get(i).get("stats").get("champLevel").asText());
                matchRecord.setWin(participants.get(i).get("stats").get("win").asBoolean());
                matchRecord.setGameId(gameId);
                matchRecord.setDate(gameDate.get(gameId));
                matchRecord.setMapName(gameMode.get(gameId));
                Integer championId = participants.get(i).get("championId").asInt();
                champions.stream()
                        .filter(c -> c.getKey().equals(championId))
                        .findFirst().ifPresent(champion -> matchRecord.setChampionName(champion.getName()));
                matchRecords.add(matchRecord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return matchRecords;
    }

    public String formatDate(String isoDate) {
        Instant instant = Instant.parse(isoDate);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return zonedDateTime.format(formatter);
    }

    public Map<String, String> getMapNameById() {
        return getStringStringMap(allMaps);
    }

    public Rank rank(String puuId) throws JsonProcessingException {
        JsonNode rankNode = objectMapper.readTree(callApi.callApiGet("/lol-ranked/v1/ranked-stats/" + puuId, getGlobalTokenAndPort.getToken(), getGlobalTokenAndPort.getPort(), null)).get("queueMap");
        Rank rank = new Rank();
        rank.setRANKED_FLEX_SR_CurrentSeason_Division(returnDivision(rankNode.get("RANKED_FLEX_SR").get("division").asText()));
        rank.setRANKED_FLEX_SR_CurrentSeason_HighestDivision(returnDivision(rankNode.get("RANKED_FLEX_SR").get("highestDivision").asText()));
        rank.setRANKED_FLEX_SR_CurrentSeason_HighestTier(position.getPosition().get(rankNode.get("RANKED_FLEX_SR").get("highestTier").asText()));
        rank.setRANKED_FLEX_SR_CurrentSeason_LeaguePoints(rankNode.get("RANKED_FLEX_SR").get("leaguePoints").asText());
        rank.setRANKED_FLEX_SR_PreviousSeasonHighestTier(position.getPosition().get(rankNode.get("RANKED_FLEX_SR").get("previousSeasonHighestTier").asText()));
        rank.setRANKED_FLEX_SR_CurrentSeason_Tier(position.getPosition().get(rankNode.get("RANKED_FLEX_SR").get("tier").asText()));
        rank.setRANKED_SOLO_5x5_CurrentSeason_Tier(position.getPosition().get(rankNode.get("RANKED_SOLO_5x5").get("tier").asText()));
        rank.setRANKED_SOLO_5x5_CurrentSeason_Division(returnDivision(rankNode.get("RANKED_SOLO_5x5").get("division").asText()));
        rank.setRANKED_SOLO_5x5_CurrentSeason_HighestDivision(returnDivision(rankNode.get("RANKED_SOLO_5x5").get("highestDivision").asText()));
        rank.setRANKED_SOLO_5x5_CurrentSeason_HighestTier(position.getPosition().get(rankNode.get("RANKED_SOLO_5x5").get("highestTier").asText()));
        rank.setRANKED_SOLO_5x5_CurrentSeason_LeaguePoints(rankNode.get("RANKED_SOLO_5x5").get("leaguePoints").asText());
        rank.setRANKED_SOLO_5x5_PreviousSeasonHighestTier(position.getPosition().get(rankNode.get("RANKED_SOLO_5x5").get("previousSeasonHighestTier").asText()));
        return rank;
    }

    public String returnDivision(String division) {
        return division.equals("NA") ? "" : division;
    }
}