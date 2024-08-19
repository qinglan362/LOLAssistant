package com.ywh.yxlmzs.controller;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.MatchesListInfo;
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


@RestController
public class MatchesListFromPuuid {
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

    @Autowired
    public MatchesListFromPuuid(GetGlobalTokenAndPort getGlobalTokenAndPort, AllChampions allChampions, AllMaps allMaps ) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.allChampions = allChampions;
        this.allMaps = allMaps;
    }

    @GetMapping("/matchesFromPuuid")
    public List<MatchesListInfo> MatchesFromPuuid(@RequestParam Map<String, Object> map) throws IOException {

        int page=Integer.parseInt(map.get("page").toString());

        int begIndex=(page-1)*18;
        int endIndex=page*18-1;

        JSONObject jsonObject = JSONObject.parseObject(getSummoners.getSummoners(Map.of("name",map.get("name"))));
        String puuId = jsonObject.getString("puuid");
        String url = "/lol-match-history/v1/products/lol/" + puuId + "/matches";
        String token = getGlobalTokenAndPort.getToken();
        String port = getGlobalTokenAndPort.getPort();

        //场次参数
        Map<String, Object> map1 = Map.of("begIndex", begIndex, "endIndex", endIndex);

        JsonNode games = objectMapper.readTree(callApi.callApiGet(url, token, port, map1)).get("games").get("games");


        List<MatchesListInfo> matchesListInfos = new ArrayList<>();
        for (JsonNode game : games) {
//            if (
//                    Objects.equals(game.get("mapId").asText(), "11") ||
//                    Objects.equals(game.get("mapId").asText(),"30") ||
//                    Objects.equals(game.get("mapId").asText(), "12")||
//                    Objects.equals(game.get("mapId").asText(), "33")
//            ) {
                String id;
                String type;
                if (!Objects.equals(game.get("queueId").asText(), "0")) {
                    id = game.get("queueId").asText();
                    type="queueId";
                } else {
                    id = game.get("mapId").asText();
                    type="mapId";
                }

                matchesListInfos.add(
                        new MatchesListInfo(
                                game.get("gameId").asText(),
                                game.get("participants").get(0).get("stats").get("win").asBoolean(),
                                getChampionNameById(game.get("participants").get(0).get("championId").asInt()),
                                formatDate(game.get("gameCreationDate").asText()),
                                getGameMapNameById(id,type),
                                game.get("participants").get(0).get("stats").get("subteamPlacement").asText()
                        )
                );
//            }
        }

        return matchesListInfos;
      }

      public String getChampionNameById(Integer id) {
          return allChampions.getList().stream().filter(champion -> champion.getKey().equals(id)).findFirst().get().getName();
      }
      public String getGameMapNameById(String id,String type) {

        if (type.equals("queueId")) {
            return allMaps.getList().stream().filter(map -> map.getId().equals(id)).findFirst().get().getName();
        }else{
            return allMaps.getList().stream().filter(map -> map.getMapId().equals(id)).findFirst().get().getName();
        }
      }
     public String formatDate(String isoDate) {
        Instant instant = Instant.parse(isoDate);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return zonedDateTime.format(formatter);
      }
    }
