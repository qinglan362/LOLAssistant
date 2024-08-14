package com.ywh.yxlmzs.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.*;
import com.ywh.yxlmzs.service.GetSummoners;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
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

import java.util.stream.StreamSupport;

@RestController
public class MatchesTFTFromPuuid {

    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    GetSummoners getSummoners;


    private GetGlobalTokenAndPort getGlobalTokenAndPort ;

    @Autowired
    public MatchesTFTFromPuuid(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @GetMapping("/matchesTFTFromPuuid")
    public  List<TFTMatchesListInfo> matchesTFTFromPuuid(@RequestParam Map<String,String> map) throws IOException {

        int page=Integer.parseInt(map.get("page"));

        int begIndex=(page-1)*18;

        //tag也可以作为参数
        Map<String, Object> map1 = Map.of("begin", begIndex, "count", 18);

        JSONObject jsonObject = JSONObject.parseObject(getSummoners.getSummoners(Map.of("name",map.get("name"))));
        String puuId = jsonObject.getString("puuid");
        JsonNode gamesJSON=objectMapper.readTree(
                callApi.callApiGet(
                        "/lol-match-history/v1/products/tft/"+puuId+"/matches",
                        getGlobalTokenAndPort.getToken(),
                        getGlobalTokenAndPort.getPort(),
                        map1
                )
        ).get("games");

        List<TFTMatchesListInfo> tftMatchesListInfos=new ArrayList<>();
        for (JsonNode game:gamesJSON) {
            JsonNode games=game.get("json");
            TFTMatchesListInfo tftMatchesListInfo=new TFTMatchesListInfo();
            tftMatchesListInfo.setName(map.get("name"));
            tftMatchesListInfo.setGameId(games.get("game_id").asText());
            tftMatchesListInfo.setDate(formatDate(games.get("game_datetime").asText()));
            JsonNode participants=games.get("participants");
            for (JsonNode participant:participants){
                if (participant.get("puuid").asText().equals(puuId)){
                    tftMatchesListInfo.setPuuId(puuId);
                    tftMatchesListInfo.setPlacement(participant.get("placement").asText());
                    List<String> tags = StreamSupport.stream(game.get("metadata").get("tags").spliterator(), false)
                            .limit(3)
                            .map(JsonNode::asText)
                            .toList();
                    tftMatchesListInfo.setTags(tags);
                    break;
                }
            }
            tftMatchesListInfos.add(tftMatchesListInfo);
        }
        return tftMatchesListInfos;

    }

    public String formatDate(String isoDate) {
        long time = Long.parseLong(isoDate);
        Instant instant = Instant.ofEpochMilli(time);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return zonedDateTime.format(formatter);
    }

    public String gameName(String puuId) throws JsonProcessingException {

          JsonNode js   = objectMapper.readTree(callApi.callApiGet(
                        "/lol-summoner/v2/summoners/puuid/"+puuId,
                        getGlobalTokenAndPort.getToken(),
                        getGlobalTokenAndPort.getPort(),
                        null
                ));
        return js.get("gameName").asText()+"#"+js.get("tagLine").asText();
    }

}
