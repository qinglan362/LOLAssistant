package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.MatchesListInfo;
import com.ywh.yxlmzs.entity.OneMatchDetail;
import com.ywh.yxlmzs.entity.Rank;
import com.ywh.yxlmzs.entity.vo.ImageAndToolTips;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class MatchOneDetail {

    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    SaveImage saveImage;

    private final  GetGlobalTokenAndPort getGlobalTokenAndPort;
    private final Position position;
    private final AllChampions allChampions;
    private final AllMaps allMaps;
    private String currentSeasonRank="";

    @Autowired
    public MatchOneDetail(GetGlobalTokenAndPort getGlobalTokenAndPort, Position position, AllChampions allChampions, AllMaps allMaps) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.position = position;
        this.allChampions = allChampions;
        this.allMaps = allMaps;
    }

    @GetMapping("/MatchOneDetail")
    public List<OneMatchDetail> MatchOneDetails(@RequestParam String gameId) throws IOException {

        JsonNode OneMatchDetails=objectMapper.readTree(callApi.callApiGet(
                "/lol-match-history/v1/games/"+gameId,
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                null
         ));

        List<OneMatchDetail> oneMatchDetails = new ArrayList<>();
        JsonNode participants = OneMatchDetails.get("participants");
        JsonNode participantIdentities = OneMatchDetails.get("participantIdentities");
        for (int i = 0; i < participants.size(); i++) {

            OneMatchDetail oneMatchDetail = new OneMatchDetail();
            String puuId=participantIdentities.get(i).get("player").get("puuid").asText();
            JsonNode rankNode = objectMapper.readTree(callApi.callApiGet("/lol-ranked/v1/ranked-stats/" + puuId, getGlobalTokenAndPort.getToken(), getGlobalTokenAndPort.getPort(), null)).get("queueMap");
            Rank rank = rank(rankNode,puuId);
            oneMatchDetail.setRank(rank);
            MatchesListInfo matchRecord = new MatchesListInfo();
            matchRecord.setGameId(gameId);
            matchRecord.setDate(formatDate(OneMatchDetails.get("gameCreationDate").asText()));
            String id;
            String type;
            if (Objects.equals(OneMatchDetails.get("queueId").asText(), "0")){
                id=OneMatchDetails.get("mapId").asText();
                type="mapId";
            }else{
                id=OneMatchDetails.get("queueId").asText();
                type="queueId";
            }

            matchRecord.setMapName(getGameMapNameById(id,type));
            matchRecord.setWin(participants.get(i).get("stats").get("win").asBoolean());
            matchRecord.setChampionName(getChampionNameById(participants.get(i).get("championId").asInt()));
            oneMatchDetail.setRatedRating(rankNode.get("CHERRY").get("ratedRating").asText());
            oneMatchDetail.setMatchesListInfo(matchRecord);
            oneMatchDetail.setGameName(participantIdentities.get(i).get("player").get("gameName").asText() + "#" + participantIdentities.get(i).get("player").get("tagLine").asText());
            oneMatchDetail.setDeaths(participants.get(i).get("stats").get("deaths").asInt());
            oneMatchDetail.setKills(participants.get(i).get("stats").get("kills").asInt());
            oneMatchDetail.setAssists(participants.get(i).get("stats").get("assists").asInt());
            oneMatchDetail.setChampLevel(participants.get(i).get("stats").get("champLevel").asText());
            oneMatchDetail.setTotalDamageTaken(participants.get(i).get("stats").get("totalDamageTaken").asText());
            oneMatchDetail.setTotalDamageDealtToChampions(participants.get(i).get("stats").get("totalDamageDealtToChampions").asText());
            oneMatchDetail.setWardsPlaced(participants.get(i).get("stats").get("wardsPlaced").asText());
            oneMatchDetail.setTotalMinionsKilled(participants.get(i).get("stats").get("totalMinionsKilled").asText());


            JsonNode itemJson = objectMapper.readTree(
                    callApi.callApiGet(
                            "/lol-game-data/assets/v1/items.json",
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null
                    )
            );
            List<String> itemIds = new ArrayList<>();
            for (int j = 0; j <=6; j++) {
               int itemId = participants.get(i).get("stats").get("item" + j).asInt();
               if (itemId != 0) {
                      itemIds.add(String.valueOf(itemId));
               }
            }
            oneMatchDetail.setItemsImage(saveImage.saveImage(itemJson,itemIds,"items", "png", "items"));

          //斗魂竞技场海克斯 这里面也有无尽狂潮的选的增幅海克斯
            JsonNode augmentJson = objectMapper.readTree(
                    callApi.callApiGet(
                            "/lol-game-data/assets/v1/cherry-augments.json",
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null
                    )
            );
            List<String> augmentIds = new ArrayList<>();
            for (int j = 1; j <=6; j++) {
                int augmentId = participants.get(i).get("stats").get("playerAugment" + j).asInt();
                if (augmentId != 0) {
                    augmentIds.add(String.valueOf(augmentId));
                }
            }
            oneMatchDetail.setAugments(saveImage.saveImage(augmentJson,augmentIds,"augment",  "png", "cherry-augments"));


            JsonNode spelljson = objectMapper.readTree(
                    callApi.callApiGet(
                            "/lol-game-data/assets/v1/summoner-spells.json",
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null
                    )
            );
            List<String> spellIds = new ArrayList<>();
            for (int j = 1; j <=2; j++) {
                int itemId = participants.get(i).get("spell" + j+"Id").asInt();
                if (itemId != 0) {
                    spellIds.add(String.valueOf(itemId));
                }
            }
            oneMatchDetail.setSpellsImage(saveImage.saveImage(spelljson,spellIds,"summonerSpells", "png", "summoner-spells"));


            if (currentSeasonRank.isEmpty()){
                currentSeasonRank = "UNRANKED";
             }
            oneMatchDetail.setRankImage("rank/"+currentSeasonRank.toLowerCase()+".png");


            JsonNode championSummaryJson = objectMapper.readTree(
                    callApi.callApiGet(
                            "/lol-game-data/assets/v1/champion-summary.json",
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null
                    )
            );
             List<String> championIds = new ArrayList<>();
             championIds.add(String.valueOf(participants.get(i).get("championId").asInt()));
             oneMatchDetail.setChampionImage(saveImage.saveImage(championSummaryJson,championIds,"championSummary", "jpg", "champion-summary").get(0));


            JsonNode perkJson = objectMapper.readTree(
                    callApi.callApiGet(
                            "/lol-game-data/assets/v1/perks.json",
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null
                    )
            );
            List<String> perkIds = new ArrayList<>();
            String mapId=OneMatchDetails.get("mapId").asText();
             if (!(mapId.equals("30")||mapId.equals("33"))){
                 for (int j = 0; j <=5; j++) {
                     int perk = participants.get(i).get("stats").get("perk" + j).asInt();
                      perkIds.add(String.valueOf(perk));
                 }
                 oneMatchDetail.setPerkImage(saveImage.saveImage(perkJson,perkIds, "perks","png", "perks"));
             }

             //斗魂竞技场名次
            String subteamPlacement = participants.get(i).get("stats").get("subteamPlacement").asText();
            oneMatchDetail.setSubteamPlacement(subteamPlacement);


            oneMatchDetails.add(oneMatchDetail);
        }
        return oneMatchDetails;
    }

    public Rank rank(JsonNode rankNode, String puuId) throws JsonProcessingException {
        Rank rank = new Rank();
        rank.setRANKED_FLEX_SR_CurrentSeason_Division(returnDivision(rankNode.get("RANKED_FLEX_SR").get("division").asText()));
        rank.setRANKED_FLEX_SR_CurrentSeason_HighestDivision(returnDivision(rankNode.get("RANKED_FLEX_SR").get("highestDivision").asText()));
        rank.setRANKED_FLEX_SR_CurrentSeason_HighestTier(position.getPosition().get(rankNode.get("RANKED_FLEX_SR").get("highestTier").asText()));
        rank.setRANKED_FLEX_SR_CurrentSeason_LeaguePoints(rankNode.get("RANKED_FLEX_SR").get("leaguePoints").asText());
        rank.setRANKED_FLEX_SR_PreviousSeasonHighestTier(position.getPosition().get(rankNode.get("RANKED_FLEX_SR").get("previousSeasonHighestTier").asText()));
        rank.setRANKED_FLEX_SR_CurrentSeason_Tier(position.getPosition().get(rankNode.get("RANKED_FLEX_SR").get("tier").asText()));
        rank.setRANKED_SOLO_5x5_CurrentSeason_Tier(position.getPosition().get(rankNode.get("RANKED_SOLO_5x5").get("tier").asText()));
        currentSeasonRank = rankNode.get("RANKED_SOLO_5x5").get("tier").asText();
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
    public String getChampionNameById(Integer id) {
        return allChampions.getList().stream().filter(champion -> champion.getKey().equals(id)).findFirst().get().getName();
    }
    public String getGameMapNameById(String id,String type) {
        if (type.equals("queueId")) {
            return  allMaps.getList().stream().filter(map -> map.getId().equals(id)).findFirst().get().getName();
        }else{
            return  allMaps.getList().stream().filter(map -> map.getMapId().equals(id)).findFirst().get().getName();
        }
    }
    public String formatDate(String isoDate) {
        Instant instant = Instant.parse(isoDate);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return zonedDateTime.format(formatter);
    }
}
