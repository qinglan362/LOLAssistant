package com.ywh.yxlmzs.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.*;
import com.ywh.yxlmzs.service.GetSummoners;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import com.ywh.yxlmzs.utils.Position;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
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
    private  Position position;

    @Autowired
    public MatchesTFTFromPuuid(GetGlobalTokenAndPort getGlobalTokenAndPort, Position position) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.position = position;
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

//        List<TFT> tfts=new ArrayList<>();
//        for (JsonNode game:gamesJSON){
//            JsonNode json=game.get("json");
//            JsonNode tags=game.get("metadata").get("tags");
//            TFT tft=new TFT();
//            List<String> tagsTempt = StreamSupport.stream(tags.spliterator(), false)
//                            .limit(3)
//                            .map(JsonNode::asText)
//                            .toList();
//            tft.setTags(tagsTempt);
//            tft.setDate(formatDate(json.get("game_datetime").asText()));
//            tft.setGameId(json.get("game_id").asText());
//            JsonNode participants=json.get("participants");
//            List<TFTOneMatchDetail> tftOneMatchDetails=new ArrayList<>();
//            for (JsonNode participant:participants){
//                TFTOneMatchDetail tftOneMatchDetail=new TFTOneMatchDetail();
//                String puuid=participant.get("puuid").asText();
//                tftOneMatchDetail.setGameName(gameName(puuid));
//                tftOneMatchDetail.setPuuId(puuid);
//                tftOneMatchDetail.setPlacement(participant.get("placement").asText());
//                tftOneMatchDetail.setLevel(participant.get("level").asText());
//                tftOneMatchDetail.setRank(rank(puuid));
//                tftOneMatchDetail.setCompanion(
//                        saveImage(
//                        "companion",
//                         participant.get("companion").get("content_ID").asText(),
//                        "png",
//                        //这个是小小英雄原画 目前还没找到游戏记录中的小图像地址
//                        "/lol-game-data/assets/v1/companions.json",
//                        "companion",
//                                ""
//                ));
//
//                List<ImageAndMessage> units=new ArrayList<>();
//                JsonNode unit=participant.get("units");
//                for (JsonNode u:unit){
//
//                     String character_id=u.get("character_id").asText();
//                     List<String> itemName = StreamSupport.stream(u.get("itemNames").spliterator(), false)
//                            .limit(3)
//                            .map(JsonNode::asText)
//                            .toList();
//
//                     ImageAndMessage imageAndMessage=new ImageAndMessage();
//
//                     imageAndMessage.setUnitImage(
//                             saveImage(
//                                        "tftChampions",
//                                        character_id,
//                                        "png",
//                                        "/lol-game-data/assets/v1/tftchampions.json",
//                                        "tftChampions",
//                                          ""
//                             )
//                     );
//
//                     if (!itemName.isEmpty()){
//                         List<String> itemImages=new ArrayList<>();
//                         for (String item:itemName){
//                             itemImages.add(
//                                     saveImage(
//                                             "tftitems",
//                                             item,
//                                             "png",
//                                             "/lol-game-data/assets/v1/tftitems.json",//这里面不只有装备 也有海克斯augment
//                                             "tftitems",
//                                             ""
//                                     )
//                             );
//                         }
//                         imageAndMessage.setItemImage(itemImages);
//                     }
//
//                    units.add(imageAndMessage);
//                }
//                tftOneMatchDetail.setUnits(units);
//
//
//               //海克斯augment
//                List<String> augments = StreamSupport.stream(participant.get("augments").spliterator(), false)
//                        .map(JsonNode::asText)
//                        .toList();
//                List<String> augmentsImage=new ArrayList<>();
//                for(String augment:augments){
//                    augmentsImage.add(saveImage(
//                            "tftaugment",
//                             augment,
//                            "png",
//                            "/lol-game-data/assets/v1/tftitems.json",//这里面不只有装备 也有海克斯augment
//                            "tftitems",
//                            ""
//                    ));
//                }
//                tftOneMatchDetail.setAugments(augmentsImage);
//
//
//                //羁绊
//                List<String> traitsImage=new ArrayList<>();
//                JsonNode traits=participant.get("traits");
//                for (JsonNode trait:traits){
//                      if (trait.get("tier_current").asInt()>0){
//                            traitsImage.add(
//                                    saveImage(
//                                            "tfttraits",
//                                            trait.get("name").asText(),
//                                            "png",
//                                            "/lol-game-data/assets/v1/tfttraits.json",
//                                            "tfttraits",
//                                             trait.get("style").asText()
//                                    )
//                            );
//                      }
//                }
//                tftOneMatchDetail.setTraits(traitsImage);
//
//                if (currentSeasonRank.isEmpty()){
//                    currentSeasonRank = "UNRANKED";
//                }
//                //网络获取
//                // tftOneMatchDetail.setRankImage("https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-shared-components/global/default/"+currentSeasonRank.toLowerCase()+".png");
//                //本地获取
//                tftOneMatchDetail.setRankImage("rank/"+currentSeasonRank.toLowerCase()+".png");
//
//
//                tftOneMatchDetails.add(tftOneMatchDetail);
//            }
//
//            tft.setTFTOneMatchDetail(tftOneMatchDetails);
//
//            tfts.add(tft);
//        }
//        return tfts;

        //根据gameId查询单独某一个比赛使用，但是TFT无法使用所以注释掉
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