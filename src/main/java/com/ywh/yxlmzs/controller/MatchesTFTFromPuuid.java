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


    private String currentSeasonRank;
    private GetGlobalTokenAndPort getGlobalTokenAndPort ;
    private  Position position;

    @Autowired
    public MatchesTFTFromPuuid(GetGlobalTokenAndPort getGlobalTokenAndPort, Position position) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.position = position;
    }

    @GetMapping("/matchesTFTFromPuuid")
    public  List<TFT>  matchesTFTFromPuuid(@RequestParam Map<String,String> map) throws IOException {

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

//         summary https://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/assets/loadouts/companions/
//        System.out.println(objectMapper.readTree(
//                callApi.callApiGet(
//                        "/lol-game-data/assets/asse/companions-summary.json",
//                        getGlobalTokenAndPort.getToken(),
//                        getGlobalTokenAndPort.getPort(),
//                        null
//                )
//        ).toPrettyString());

         List<TFT> tfts=new ArrayList<>();
        for (JsonNode game:gamesJSON){
            JsonNode json=game.get("json");
            JsonNode tags=game.get("metadata").get("tags");
            TFT tft=new TFT();
            List<String> tagsTempt = StreamSupport.stream(tags.spliterator(), false)
                            .limit(3)
                            .map(JsonNode::asText)
                            .toList();
            tft.setTags(tagsTempt);
            tft.setDate(formatDate(json.get("game_datetime").asText()));
            tft.setGameId(json.get("game_id").asText());
            JsonNode participants=json.get("participants");
            List<TFTOneMatchDetail> tftOneMatchDetails=new ArrayList<>();
            for (JsonNode participant:participants){
                TFTOneMatchDetail tftOneMatchDetail=new TFTOneMatchDetail();
                String puuid=participant.get("puuid").asText();
                tftOneMatchDetail.setGameName(gameName(puuid));
                tftOneMatchDetail.setPuuId(puuid);
                tftOneMatchDetail.setPlacement(participant.get("placement").asText());
                tftOneMatchDetail.setLevel(participant.get("level").asText());
                tftOneMatchDetail.setRank(rank(puuid));
                tftOneMatchDetail.setCompanion(
                        saveImage(
                        "companion",
                         participant.get("companion").get("content_ID").asText(),
                        "png",
                        //这个是小小英雄原画 目前还没找到游戏记录中的小图像地址
                        "/lol-game-data/assets/v1/companions.json",
                        "companion",
                                ""
                ));

                List<ImageAndMessage> units=new ArrayList<>();
                JsonNode unit=participant.get("units");
                for (JsonNode u:unit){

                     String character_id=u.get("character_id").asText();
                     List<String> itemName = StreamSupport.stream(u.get("itemNames").spliterator(), false)
                            .limit(3)
                            .map(JsonNode::asText)
                            .toList();

                     ImageAndMessage imageAndMessage=new ImageAndMessage();

                     imageAndMessage.setUnitImage(
                             saveImage(
                                        "tftChampions",
                                        character_id,
                                        "png",
                                        "/lol-game-data/assets/v1/tftchampions.json",
                                        "tftChampions",
                                          ""
                             )
                     );

                     if (!itemName.isEmpty()){
                         List<String> itemImages=new ArrayList<>();
                         for (String item:itemName){
                             itemImages.add(
                                     saveImage(
                                             "tftitems",
                                             item,
                                             "png",
                                             "/lol-game-data/assets/v1/tftitems.json",//这里面不只有装备 也有海克斯augment
                                             "tftitems",
                                             ""
                                     )
                             );
                         }
                         imageAndMessage.setItemImage(itemImages);
                     }

                    units.add(imageAndMessage);
                }
                tftOneMatchDetail.setUnits(units);


               //海克斯augment
                List<String> augments = StreamSupport.stream(participant.get("augments").spliterator(), false)
                        .map(JsonNode::asText)
                        .toList();
                List<String> augmentsImage=new ArrayList<>();
                for(String augment:augments){
                    augmentsImage.add(saveImage(
                            "tftaugment",
                             augment,
                            "png",
                            "/lol-game-data/assets/v1/tftitems.json",//这里面不只有装备 也有海克斯augment
                            "tftitems",
                            ""
                    ));
                }
                tftOneMatchDetail.setAugments(augmentsImage);

                List<String> traitsImage=new ArrayList<>();
                JsonNode traits=participant.get("traits");
                for (JsonNode trait:traits){
                      if (trait.get("tier_current").asInt()>0){
                            traitsImage.add(
                                    saveImage(
                                            "tfttraits",
                                            trait.get("name").asText(),
                                            "png",
                                            "/lol-game-data/assets/v1/tfttraits.json",
                                            "tfttraits",
                                             trait.get("style").asText()
                                    )
                            );
                      }
                }
                tftOneMatchDetail.setTraits(traitsImage);

                if (currentSeasonRank.isEmpty()){
                    currentSeasonRank = "UNRANKED";
                }
                tftOneMatchDetail.setRankImage("https://raw.communitydragon.org/latest/plugins/rcp-fe-lol-shared-components/global/default/"+currentSeasonRank.toLowerCase()+".png");

                tftOneMatchDetails.add(tftOneMatchDetail);
            }

            tft.setTFTOneMatchDetail(tftOneMatchDetails);

            tfts.add(tft);
        }
        return tfts;

        //根据gameId查询单独某一个比赛使用，但是TFT无法使用所以注释掉
//        List<TFTMatchesListInfo> tftMatchesListInfos=new ArrayList<>();
//        for (JsonNode game:gamesJSON) {
//            JsonNode games=game.get("json");
//            TFTMatchesListInfo tftMatchesListInfo=new TFTMatchesListInfo();
//            tftMatchesListInfo.setGameId(games.get("game_id").asText());
//            tftMatchesListInfo.setDate(formatDate(games.get("game_datetime").asText()));
//            JsonNode participants=games.get("participants");
//            for (JsonNode participant:participants){
//                if (participant.get("puuid").asText().equals(puuId)){
//                    tftMatchesListInfo.setPuuId(puuId);
//                    tftMatchesListInfo.setPlacement(participant.get("placement").asText());
//                    List<String> tags = StreamSupport.stream(game.get("metadata").get("tags").spliterator(), false)
//                            .limit(3)
//                            .map(JsonNode::asText)
//                            .toList();
//                    tftMatchesListInfo.setTags(tags);
//                    break;
//                }
//            }
//            tftMatchesListInfos.add(tftMatchesListInfo);
//        }
//        return tftMatchesListInfos;

    }
    public String saveImage(String floderName,
                            String id,
                            String houzhui,
                            String ImageUrl,
                            String type,
                            String style
    ) throws IOException {

        if (!Files.exists(Paths.get(System.getProperty("user.dir")+"/images/"+floderName+"/"+id+ "."+houzhui))) {
            JsonNode typeImage  = objectMapper.readTree(
                    callApi.callApiGet(
                            ImageUrl,
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null
                    )
            );
            String url="";
            for (JsonNode ti : typeImage) {
                if (type.equals("companion")) {
                    if (ti.get("contentId").asText().equals(id)) {
                        url=ti.get("loadoutsIcon").asText();
                    }
                }
                if (type.equals("tftChampions")) {
                    if (ti.get("character_record").get("character_id").asText().equals(id)) {
                        url=ti.get("character_record").get("squareIconPath").asText();
                    }
                }
                if (type.equals("tftitems")) {
                    if (ti.get("nameId").asText().equals(id)) {
                        url=ti.get("squareIconPath").asText();
                    }
                }
                if (type.equals("tfttraits")) {
                    if (ti.get("trait_id").asText().equals(id)) {
                        url=ti.get("icon_path").asText();
                    }
                }
            }
            byte[] imageBytes = callApi.callApiGetImage(
                    url,
                    getGlobalTokenAndPort.getToken(),
                    getGlobalTokenAndPort.getPort(),
                    null);
            String directoryPath = System.getProperty("user.dir")+"/images/"+floderName+"/";
            Files.createDirectories(Paths.get(directoryPath));
            try (FileOutputStream fos = new FileOutputStream(directoryPath +id + "."+houzhui)) {
                fos.write(imageBytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            BufferedImage image = ImageIO.read(new File(directoryPath +id + "."+houzhui));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, houzhui, baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            return  base64Image;
        }else{
            BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir")+"/images/"+floderName+"/"+id + "."+houzhui));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, houzhui, baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            return base64Image;
        }
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

    public TFTRank rank(String puuId) throws JsonProcessingException {
        JsonNode rankNode = objectMapper.readTree(callApi.callApiGet("/lol-ranked/v1/ranked-stats/" + puuId, getGlobalTokenAndPort.getToken(), getGlobalTokenAndPort.getPort(), null)).get("queueMap");
        TFTRank rank = new TFTRank();

        rank.setRANKED_TFT_CurrentSeason_Division(returnDivision(rankNode.get("RANKED_TFT").get("division").asText()));
        rank.setRANKED_TFT_CurrentSeason_Tier(position.getPosition().get(rankNode.get("RANKED_TFT").get("tier").asText()));
        rank.setRANKED_TFT_CurrentSeason_HighestDivision(returnDivision(rankNode.get("RANKED_TFT").get("highestDivision").asText()));
        rank.setRANKED_TFT_CurrentSeason_HighestTier(position.getPosition().get(rankNode.get("RANKED_TFT").get("highestTier").asText()));
        rank.setRANKED_TFT_CurrentSeason_LeaguePoints(rankNode.get("RANKED_TFT").get("leaguePoints").asText());
        rank.setRANKED_TFT_PreviousSeasonHighestTier(position.getPosition().get(rankNode.get("RANKED_TFT").get("previousSeasonHighestTier").asText()));

        currentSeasonRank = rankNode.get("RANKED_TFT").get("tier").asText();

        rank.setRANKED_TFT_DOUBLE_UP_CurrentSeason_Division(returnDivision(rankNode.get("RANKED_TFT_DOUBLE_UP").get("division").asText()));
        rank.setRANKED_TFT_DOUBLE_UP_CurrentSeason_Tier(position.getPosition().get(rankNode.get("RANKED_TFT_DOUBLE_UP").get("tier").asText()));
        rank.setRANKED_TFT_DOUBLE_UP_CurrentSeason_HighestDivision(returnDivision(rankNode.get("RANKED_TFT_DOUBLE_UP").get("highestDivision").asText()));
        rank.setRANKED_TFT_DOUBLE_UP_CurrentSeason_HighestTier(position.getPosition().get(rankNode.get("RANKED_TFT_DOUBLE_UP").get("highestTier").asText()));
        rank.setRANKED_TFT_DOUBLE_UP_CurrentSeason_LeaguePoints(rankNode.get("RANKED_TFT_DOUBLE_UP").get("leaguePoints").asText());
        rank.setRANKED_TFT_DOUBLE_UP_PreviousSeasonHighestTier(position.getPosition().get(rankNode.get("RANKED_TFT_DOUBLE_UP").get("previousSeasonHighestTier").asText()));

        rank.setRANKED_TFT_TURBO_CurrentSeason_Division(returnDivision(rankNode.get("RANKED_TFT_TURBO").get("division").asText()));
        rank.setRANKED_TFT_TURBO_CurrentSeason_Tier(position.getPosition().get(rankNode.get("RANKED_TFT_TURBO").get("tier").asText()));
        rank.setRANKED_TFT_TURBO_CurrentSeason_HighestDivision(returnDivision(rankNode.get("RANKED_TFT_TURBO").get("highestDivision").asText()));
        rank.setRANKED_TFT_TURBO_CurrentSeason_HighestTier(position.getPosition().get(rankNode.get("RANKED_TFT_TURBO").get("highestTier").asText()));
        rank.setRANKED_TFT_TURBO_CurrentSeason_LeaguePoints(rankNode.get("RANKED_TFT_TURBO").get("leaguePoints").asText());
        rank.setRANKED_TFT_TURBO_PreviousSeasonHighestTier(position.getPosition().get(rankNode.get("RANKED_TFT_TURBO").get("previousSeasonHighestTier").asText()));

      return rank;
    }
    public String returnDivision(String division) {
        return division.equals("NA") ? "" : division;
    }
}
