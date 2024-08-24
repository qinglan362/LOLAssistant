package com.ywh.yxlmzs.controller;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.ImageAndMessage;
import com.ywh.yxlmzs.entity.TFT;
import com.ywh.yxlmzs.entity.TFTOneMatchDetail;
import com.ywh.yxlmzs.entity.TFTRank;
import com.ywh.yxlmzs.service.GetSummoners;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import com.ywh.yxlmzs.utils.Position;
import com.ywh.yxlmzs.utils.SaveImage;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@RestController
public class MatchOneTFTDetail {
    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    GetSummoners getSummoners;
    @Resource
    SaveImage saveImages;

    private String currentSeasonRank;
    private GetGlobalTokenAndPort getGlobalTokenAndPort;
    private Position position;

    @Autowired
    public MatchOneTFTDetail(GetGlobalTokenAndPort getGlobalTokenAndPort,Position position) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.position = position;
    }

    @GetMapping("/TFTMatchOneDetail")
    public TFT TFTMatchOneDetail(@RequestParam Map<String,String> data) throws IOException {

        //{currentPage=1, index=0}
        int begIndex=(Integer.parseInt(data.get("currentPage"))-1)*18+Integer.parseInt(data.get("index"));

        //tag也可以作为参数
        //好像不好用呢

        Map<String, Object> map1 = Map.of("begin", begIndex, "count", 1);

        JSONObject jsonObject = JSONObject.parseObject(getSummoners.getSummoners(Map.of("name",data.get("name"))));
        String puuId = jsonObject.getString("puuid");
        JsonNode gamesJSON=objectMapper.readTree(
                callApi.callApiGet(
                        "/lol-match-history/v1/products/tft/"+puuId+"/matches",
                        getGlobalTokenAndPort.getToken(),
                        getGlobalTokenAndPort.getPort(),
                        map1
                )
        ).get("games");

            JsonNode json=gamesJSON.get(0).get("json");
            JsonNode tags=gamesJSON.get(0).get("metadata").get("tags");
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

                JsonNode companion=objectMapper.readTree(
                        callApi.callApiGet(
                                "/lol-game-data/assets/v1/companions.json",
                                getGlobalTokenAndPort.getToken(),
                                getGlobalTokenAndPort.getPort(),
                                null
                        )
                );
                tftOneMatchDetail.setCompanion(
                        saveImages.saveImage(
                                companion,
                                List.of(participant.get("companion").get("content_ID").asText()),
                                "companion",
                                "png",
                                "companion"
                        ).get(0));


                JsonNode tftUnits= objectMapper.readTree(callApi.callApiGet(
                        "/lol-game-data/assets/v1/tftchampions.json",
                        getGlobalTokenAndPort.getToken(),
                        getGlobalTokenAndPort.getPort(),
                        null
                ));
                JsonNode tftItems= objectMapper.readTree(callApi.callApiGet(
                        "/lol-game-data/assets/v1/tftitems.json",
                        getGlobalTokenAndPort.getToken(),
                        getGlobalTokenAndPort.getPort(),
                        null
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
                             saveImages.saveImage(
                                        tftUnits,
                                        List.of(character_id),
                                        "tftChampions",
                                        "png",
                                        "tftChampions"
                             ).get(0)
                     );

                    if (!itemName.isEmpty()){
                        System.out.println(itemName);
                        List<String> itemIds = new ArrayList<>(itemName);
                         imageAndMessage.setItemImage(saveImages.saveImage(
                                    tftItems,
                                    itemIds,
                                    "tftitems",
                                    "png",
                                    "tftitems"
                         ));
                     }

                    units.add(imageAndMessage);
                }
                tftOneMatchDetail.setUnits(units);


               //海克斯augment
                List<String> augments = StreamSupport.stream(participant.get("augments").spliterator(), false)
                        .map(JsonNode::asText)
                        .toList();
                tftOneMatchDetail.setAugments(saveImages.saveImage(
                        tftItems,
                        augments,
                        "tftitems",
                        "png",
                        "tftitems"
                ));


                //羁绊
                JsonNode traitsJson=objectMapper.readTree(callApi.callApiGet(
                        "/lol-game-data/assets/v1/tfttraits.json",
                        getGlobalTokenAndPort.getToken(),
                        getGlobalTokenAndPort.getPort(),
                        null
                ));
                List<String> traitsImage=new ArrayList<>();
                JsonNode traits=participant.get("traits");
                for (JsonNode trait:traits){
                      if (trait.get("tier_current").asInt()>0){
                            traitsImage.add(trait.get("name").asText());
                      }
                }
                System.out.println(traitsImage);
                tftOneMatchDetail.setTraits(saveImages.saveImage(
                        traitsJson,
                        traitsImage,
                        "tfttraits",
                        "png",
                        "tfttraits"
                ));

                if (currentSeasonRank.isEmpty()){
                    currentSeasonRank = "UNRANKED";
                }

                tftOneMatchDetail.setRankImage("rank/"+currentSeasonRank.toLowerCase()+".png");


                tftOneMatchDetails.add(tftOneMatchDetail);
            }

            tft.setTFTOneMatchDetail(tftOneMatchDetails);

        return tft;
    }
    public String saveImage(String folderName,
                            String id,
                            String extension,
                            String imageUrl,
                            String type,
                            String style
    ) throws IOException {

        String filePath = System.getProperty("user.dir") + "/images/" + folderName + "/" + id + "." + extension;

        if (!Files.exists(Paths.get(filePath))) {
            JsonNode typeImage = objectMapper.readTree(
                    callApi.callApiGet(
                            imageUrl,
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null
                    )
            );

            String url = "";
            for (JsonNode ti : typeImage) {
                if (type.equals("companion") && ti.get("contentId").asText().equals(id)) {
                    url = ti.get("loadoutsIcon").asText();
                } else if (type.equals("tftChampions") && ti.get("character_record").get("character_id").asText().equals(id)) {
                    url = ti.get("character_record").get("squareIconPath").asText();
                } else if (type.equals("tftitems") && ti.get("nameId").asText().equals(id)) {
                    url = ti.get("squareIconPath").asText();
                } else if (type.equals("tfttraits") && ti.get("trait_id").asText().equals(id)) {
                    url = ti.get("icon_path").asText();
                }
            }

            byte[] imageBytes = callApi.callApiGetImage(
                    url,
                    getGlobalTokenAndPort.getToken(),
                    getGlobalTokenAndPort.getPort(),
                    null);

            String directoryPath = System.getProperty("user.dir") + "/images/" + folderName + "/";
            Files.createDirectories(Paths.get(directoryPath));

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return folderName + "/" + id + "." + extension;
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
