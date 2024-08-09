package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.MatchesListInfo;
import com.ywh.yxlmzs.entity.OneMatchDetail;
import com.ywh.yxlmzs.entity.Rank;
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
import java.util.Map;

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

        System.out.println(OneMatchDetails.toPrettyString());

        List<OneMatchDetail> oneMatchDetails = new ArrayList<>();
        JsonNode participants = OneMatchDetails.get("participants");
        JsonNode participantIdentities = OneMatchDetails.get("participantIdentities");
        for (int i = 0; i < participants.size(); i++) {

            OneMatchDetail oneMatchDetail = new OneMatchDetail();
            Rank rank = rank(participantIdentities.get(i).get("player").get("puuid").asText());
            oneMatchDetail.setRank(rank);
            MatchesListInfo matchRecord = new MatchesListInfo();
            matchRecord.setGameId(gameId);
            matchRecord.setDate(formatDate(OneMatchDetails.get("gameCreationDate").asText()));
            matchRecord.setMapName(getGameMapNameById(OneMatchDetails.get("mapId").asText()));
            matchRecord.setWin(participants.get(i).get("stats").get("win").asBoolean());
            matchRecord.setChampionName(getChampionNameById(participants.get(i).get("championId").asInt()));
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

            List<String> itemsImage = new ArrayList<>();
            for (int j = 0; j <=6; j++) {
               int itemId = participants.get(i).get("stats").get("item" + j).asInt();
               if (itemId != 0) {
                    String Image =saveImage.saveImage("items", String.valueOf(itemId), "png", "items");
                    itemsImage.add(Image);
               }
            }
            oneMatchDetail.setItemsImage(itemsImage);

            List<String> spellImage = new ArrayList<>();
            for (int j = 1; j <=2; j++) {
                int itemId = participants.get(i).get("spell" + j+"Id").asInt();
                if (itemId != 0) {
                    String Image =saveImage.saveImage("summonerSpells", String.valueOf(itemId), "png", "summoner-spells");
                    spellImage.add(Image);
                }
            }
            oneMatchDetail.setSpellsImage(spellImage);

            //段位 网络获取
            if (currentSeasonRank.equals("")){
                currentSeasonRank = "UNRANKED";
             }
            oneMatchDetail.setRankImage("https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-shared-components/global/default/"+currentSeasonRank.toLowerCase()+".png");


//              获取头像存本地
             String championImage = "";
             int itemId = participants.get(i).get("championId").asInt();
             championImage =saveImage.saveImage("championSummary", String.valueOf(itemId), "jpg", "champion-summary");
             if (itemId==119||itemId==76){
                 //天杀的 鬼知道德莱文和豹女为什么不行！！！！！
                 oneMatchDetail.setChampionImage("https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/"+itemId+".png");
             }else{
                 oneMatchDetail.setChampionImage(championImage);
             }

//            获取头像网络直接获取
//            int championId = participants.get(i).get("championId").asInt();
//            String championImage = "https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1/champion-icons/"+championId+".png";
//            oneMatchDetail.setChampionImage(championImage);

            String mapId=OneMatchDetails.get("mapId").asText();
             if (!(mapId.equals("30")||mapId.equals("33"))){
                 List<String> perkStyleImage = new ArrayList<>();
                 for (int j = 0; j <=5; j++) {
                     int perk = participants.get(i).get("stats").get("perk" + j).asInt();
                     String Image =saveImage.saveImage("perks", String.valueOf(perk), "png", "perks");
                     perkStyleImage.add(Image);
                 }
                 oneMatchDetail.setPerkImage(perkStyleImage);
             }

             //斗魂竞技场名次
            String subteamPlacement = participants.get(i).get("stats").get("subteamPlacement").asText();
            oneMatchDetail.setSubteamPlacement(subteamPlacement);


            oneMatchDetails.add(oneMatchDetail);
        }
        return oneMatchDetails;
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
    public String getGameMapNameById(String mapId) {
        return allMaps.getList().stream().filter(map -> map.getMapId().equals(mapId)).findFirst().get().getName();
    }
    public String formatDate(String isoDate) {
        Instant instant = Instant.parse(isoDate);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return zonedDateTime.format(formatter);
    }
}
