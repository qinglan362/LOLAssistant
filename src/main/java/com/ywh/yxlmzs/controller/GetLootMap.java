package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.recipes;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class GetLootMap {

    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public GetLootMap(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @GetMapping("/getLootMap")
    public List<recipes> getLootMap(@RequestParam String message) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(callApi.callApiGet("/lol-loot/v1/player-loot-map",
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                null
        ));

        List<recipes> recipesList = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            switch (message) {
                case "champion" -> {
                    if (node.get("displayCategories").asText().equals("CHAMPION")) {
                        recipes recipes = new recipes();
                        recipes.setStoreItemId(node.get("storeItemId").asText());
                        recipes.setDisenchantValue(node.get("disenchantValue").asText());
                        recipes.setUpgradeEssenceValue(node.get("upgradeEssenceValue").asText());
                        recipes.setValue(node.get("value").asText());
                        recipes.setCount(node.get("count").asText());
                        recipes.setChampionName(node.get("itemDesc").asText());
                        recipes.setLootName(node.get("lootName").asText());
                        recipes.setDisplayCategories(node.get("displayCategories").asText());
                        recipes.setIsRental(node.get("isRental").asBoolean());

                        setImagePath("champion", node.get("storeItemId").asText(), node.get("splashPath").asText(), "jpg");

                        recipes.setBase64Image("champion/" + node.get("storeItemId") + ".jpg");

                        recipesList.add(recipes);
                    }
                }
                case "skin" -> {
                    if (node.get("displayCategories").asText().equals("SKIN")) {
                        recipes recipes = new recipes();
                        recipes.setStoreItemId(node.get("storeItemId").asText());
                        recipes.setDisenchantValue(node.get("disenchantValue").asText());
                        recipes.setUpgradeEssenceValue(node.get("upgradeEssenceValue").asText());
                        recipes.setValue(node.get("value").asText());
                        recipes.setCount(node.get("count").asText());
                        recipes.setChampionName(node.get("itemDesc").asText());
                        recipes.setLootName(node.get("lootName").asText());
                        recipes.setDisplayCategories(node.get("displayCategories").asText());
                        recipes.setIsRental(node.get("isRental").asBoolean());

                        setImagePath("skin", node.get("storeItemId").asText(), node.get("splashPath").asText(), "jpg");

                        recipes.setBase64Image("skin/" + node.get("storeItemId") + ".jpg");

                        recipesList.add(recipes);
                    }
                }
                case "wardskin" -> {
                    if (node.get("displayCategories").asText().equals("WARDSKIN")) {
                        recipes recipes = new recipes();
                        recipes.setStoreItemId(node.get("storeItemId").asText());
                        recipes.setDisenchantValue(node.get("disenchantValue").asText());
                        recipes.setUpgradeEssenceValue(node.get("upgradeEssenceValue").asText());
                        recipes.setValue(node.get("value").asText());
                        recipes.setCount(node.get("count").asText());
                        recipes.setChampionName(node.get("itemDesc").asText());
                        recipes.setLootName(node.get("lootName").asText());
                        recipes.setDisplayCategories(node.get("type").asText());
                        recipes.setIsRental(node.get("isRental").asBoolean());

                        setImagePath("wardskin", node.get("storeItemId").asText(), node.get("splashPath").asText(), "png");

                        recipes.setBase64Image("wardskin/" + node.get("storeItemId") + ".png");

                        recipesList.add(recipes);
                    }
                }
                case "eternals" -> {
                    if (node.get("displayCategories").asText().equals("ETERNALS")) {
                        int champId = Integer.parseInt(Objects.requireNonNull(extractChampId(String.valueOf(node.get("tags")))));
                        long rootId = 0L;
                        if (node.get("type").asText().equals("STATSTONE_SHARD")) {
                            rootId = Long.parseLong(node.get("lootId").asText().split("_")[2]);
                        } else if (node.get("type").asText().equals("STATSTONE")) {
                            rootId = Long.parseLong(node.get("lootId").asText().split("_")[1]);
                        }
                        recipes recipes = new recipes();

                        recipes.setStoreItemId(Long.toString(rootId));
                        recipes.setDisenchantValue(node.get("disenchantValue").asText());
                        recipes.setUpgradeEssenceValue(node.get("upgradeEssenceValue").asText());
                        recipes.setValue(node.get("value").asText());
                        recipes.setCount(node.get("count").asText());
                        recipes.setChampionName(node.get("localizedName").asText());
                        recipes.setLootName(node.get("lootName").asText());
                        recipes.setDisplayCategories(node.get("type").asText());
                        recipes.setIsRental(node.get("isRental").asBoolean());

                        setImagePath("champion", String.valueOf(champId), node.get("splashPath").asText(), "jpg");

                        recipes.setBase64Image("champion/" + champId + ".jpg");

                        recipesList.add(recipes);
                    }
                }
                case "summonerricon" -> {
                    //图标
                    if (node.get("displayCategories").asText().equals("SUMMONERICON")) {
                        recipes recipes = new recipes();
                        recipes.setStoreItemId(node.get("storeItemId").asText());
                        recipes.setDisenchantValue(node.get("disenchantValue").asText());
                        recipes.setUpgradeEssenceValue(node.get("upgradeEssenceValue").asText());
                        recipes.setValue(node.get("value").asText());
                        recipes.setCount(node.get("count").asText());
                        recipes.setChampionName(node.get("localizedName").asText());
                        recipes.setLootName(node.get("lootName").asText());
                        recipes.setDisplayCategories(node.get("type").asText());
                        recipes.setIsRental(node.get("isRental").asBoolean());

                        setImagePath("summonerricon", node.get("storeItemId").asText(), node.get("splashPath").asText(), "jpg");

                        recipes.setBase64Image("summonerricon/" + node.get("storeItemId") + ".jpg");

                        recipesList.add(recipes);
                    }
                }
                case "emote" -> {
                    if (node.get("displayCategories").asText().equals("EMOTE")) {
                        recipes recipes = new recipes();
                        recipes.setStoreItemId(node.get("storeItemId").asText());
                        recipes.setDisenchantValue(node.get("disenchantValue").asText());
                        recipes.setUpgradeEssenceValue(node.get("upgradeEssenceValue").asText());
                        recipes.setValue(node.get("value").asText());
                        recipes.setCount(node.get("count").asText());
                        recipes.setChampionName(node.get("localizedName").asText());
                        recipes.setLootName(node.get("lootName").asText());
                        recipes.setDisplayCategories(node.get("type").asText());
                        recipes.setIsRental(node.get("isRental").asBoolean());

                        setImagePath("emote", node.get("storeItemId").asText(), node.get("splashPath").asText(), "png");

                        recipes.setBase64Image("emote/" + node.get("storeItemId") + ".png");

                        recipesList.add(recipes);
                    }
                }
                //猜测的小小英雄  待完善 完善
                case "companion" -> {
                    if (node.get("displayCategories").asText().equals("COMPANION")) {
                        recipes recipes = new recipes();
                        recipes.setStoreItemId(node.get("storeItemId").asText());
                        recipes.setDisenchantValue(node.get("disenchantValue").asText());
                        recipes.setUpgradeEssenceValue(node.get("upgradeEssenceValue").asText());
                        recipes.setValue(node.get("value").asText());
                        recipes.setCount(node.get("count").asText());
                        recipes.setChampionName(node.get("localizedName").asText());
                        recipes.setLootName(node.get("lootName").asText());
                        recipes.setDisplayCategories(node.get("type").asText());
                        recipes.setIsRental(node.get("isRental").asBoolean());

                        setImagePath("companion", node.get("storeItemId").asText(), node.get("splashPath").asText(), "png");

                        recipes.setBase64Image("companion/" + node.get("storeItemId") + ".png");

                        recipesList.add(recipes);
                    }
                }
            }

        }
        return recipesList;
    }
    public static String extractChampId(String input) {
        Pattern pattern = Pattern.compile("champId=(\\d+)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public void  setImagePath(String floderName, String id,String url,String extension) throws IOException {
        String filePath = System.getProperty("user.dir") + "/images/"+floderName+"/" + id + "."+extension;
        if (!Files.exists(Paths.get(filePath))) {
            byte[] imageBytes = callApi.callApiGetImage(
                    url,
                    getGlobalTokenAndPort.getToken(),
                    getGlobalTokenAndPort.getPort(),
                    null);
            String directoryPath = System.getProperty("user.dir") + "/images/"+floderName+"/";
            Files.createDirectories(Paths.get(directoryPath));
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
