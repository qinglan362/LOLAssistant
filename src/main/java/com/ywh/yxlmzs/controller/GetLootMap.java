package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.LongValue;
import com.ywh.yxlmzs.entity.recipes;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import lombok.val;
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
import java.util.*;

@RestController
public class GetLootMap {

    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;

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
        JsonNode  JS =objectMapper.readTree(
                callApi.callApiGet("/lol-loot/v1/player-display-categories",
                        getGlobalTokenAndPort.getToken(),
                        getGlobalTokenAndPort.getPort(),
                        null
                )
        );
        for (JsonNode node : JS) {
                System.out.println(node.toPrettyString());
        }
        List<recipes> recipesList = new ArrayList<>();
        for (JsonNode node : jsonNode) {
            if (message.equals("champion")) {
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
                    if (!Files.exists(Paths.get(System.getProperty("user.dir")+"/images/champion/"+node.get("storeItemId") + ".jpg"))) {
                        String url = node.get("splashPath").asText();
                        byte[] imageBytes = callApi.callApiGetImage(url,
                                getGlobalTokenAndPort.getToken(),
                                getGlobalTokenAndPort.getPort(),
                                null);
                        String directoryPath = System.getProperty("user.dir")+"/images/champion/";
                        Files.createDirectories(Paths.get(directoryPath));
                        try (FileOutputStream fos = new FileOutputStream(directoryPath +node.get("storeItemId") + ".jpg")) {
                            fos.write(imageBytes);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        BufferedImage image = ImageIO.read(new File(directoryPath +node.get("storeItemId") + ".jpg"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }else{
                        BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir")+"/images/champion/"+node.get("storeItemId") + ".jpg"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }
                    recipesList.add(recipes);
                }
            }
            else if (message.equals("skin")){
                if (node.get("displayCategories").asText().equals("SKIN")) {
                    System.out.println(node.toPrettyString());
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
                    if (!Files.exists(Paths.get(System.getProperty("user.dir")+"/images/skin/"+node.get("storeItemId") + ".jpg"))) {
                        String url = node.get("splashPath").asText();
                        byte[] imageBytes = callApi.callApiGetImage(url,
                                getGlobalTokenAndPort.getToken(),
                                getGlobalTokenAndPort.getPort(),
                                null);
                        String directoryPath = System.getProperty("user.dir")+"/images/skin/";
                        Files.createDirectories(Paths.get(directoryPath));
                        try (FileOutputStream fos = new FileOutputStream(directoryPath +node.get("storeItemId") + ".jpg")) {
                            fos.write(imageBytes);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        BufferedImage image = ImageIO.read(new File(directoryPath +node.get("storeItemId") + ".jpg"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }else{
                        BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir")+"/images/skin/"+node.get("storeItemId") + ".jpg"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }
                    recipesList.add(recipes);
                }
            }
            else if (message.equals("wardskin")){
                if (node.get("displayCategories").asText().equals("WARDSKIN")) {
                    System.out.println(node.toPrettyString());
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
                    if (!Files.exists(Paths.get(System.getProperty("user.dir")+"/images/wardskin/"+node.get("storeItemId") + ".png"))) {
                        String url = node.get("splashPath").asText();
                        byte[] imageBytes = callApi.callApiGetImage(url,
                                getGlobalTokenAndPort.getToken(),
                                getGlobalTokenAndPort.getPort(),
                                null);
                        String directoryPath = System.getProperty("user.dir")+"/images/wardskin/";
                        Files.createDirectories(Paths.get(directoryPath));
                        try (FileOutputStream fos = new FileOutputStream(directoryPath +node.get("storeItemId") + ".png")) {
                            fos.write(imageBytes);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        BufferedImage image = ImageIO.read(new File(directoryPath +node.get("storeItemId") + ".png"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }else{
                        BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir")+"/images/wardskin/"+node.get("storeItemId") + ".png"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }
                    recipesList.add(recipes);
                }
            }
            else if (message.equals("eternals")){
                if (node.get("displayCategories").asText().equals("ETERNALS")) {
                    Long rootId= 0L;
                    if (node.get("type").asText().equals("STATSTONE_SHARD")){
                        rootId=Long.valueOf(node.get("lootId").asText().split("_")[2]);
                    }else if (node.get("type").asText().equals("STATSTONE")){
                        rootId=Long.valueOf(node.get("lootId").asText().split("_")[1]);
                    }
                    recipes recipes = new recipes();

                    recipes.setStoreItemId(rootId.toString());
                    recipes.setDisenchantValue(node.get("disenchantValue").asText());
                    recipes.setUpgradeEssenceValue(node.get("upgradeEssenceValue").asText());
                    recipes.setValue(node.get("value").asText());
                    recipes.setCount(node.get("count").asText());
                    recipes.setChampionName(node.get("localizedName").asText());
                    recipes.setLootName(node.get("lootName").asText());
                    recipes.setDisplayCategories(node.get("type").asText());
                    recipes.setIsRental(node.get("isRental").asBoolean());
                    if (!Files.exists(Paths.get(System.getProperty("user.dir")+"/images/eternals/"+rootId + ".jpg"))) {
                        String url = node.get("splashPath").asText();
                        byte[] imageBytes = callApi.callApiGetImage(url,
                                getGlobalTokenAndPort.getToken(),
                                getGlobalTokenAndPort.getPort(),
                                null);
                        String directoryPath = System.getProperty("user.dir")+"/images/eternals/";
                        Files.createDirectories(Paths.get(directoryPath));
                        try (FileOutputStream fos = new FileOutputStream(directoryPath +rootId + ".jpg")) {
                            fos.write(imageBytes);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        BufferedImage image = ImageIO.read(new File(directoryPath +rootId + ".jpg"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }else{
                        BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir")+"/images/eternals/"+rootId + ".jpg"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }
                    recipesList.add(recipes);
                }
            }
            else if (message.equals("summonerricon")){
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
                    if (!Files.exists(Paths.get(System.getProperty("user.dir")+"/images/summonerricon/"+node.get("storeItemId") + ".jpg"))) {
                        String url = node.get("splashPath").asText();
                        byte[] imageBytes = callApi.callApiGetImage(url,
                                getGlobalTokenAndPort.getToken(),
                                getGlobalTokenAndPort.getPort(),
                                null);
                        String directoryPath = System.getProperty("user.dir")+"/images/summonerricon/";
                        Files.createDirectories(Paths.get(directoryPath));
                        try (FileOutputStream fos = new FileOutputStream(directoryPath +node.get("storeItemId") + ".jpg")) {
                            fos.write(imageBytes);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        BufferedImage image = ImageIO.read(new File(directoryPath +node.get("storeItemId") + ".jpg"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }else{
                        BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir")+"/images/summonerricon/"+node.get("storeItemId") + ".jpg"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "jpg", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }
                    recipesList.add(recipes);
                }
            }
            else if (message.equals("emote")){
                //图标
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
                    if (!Files.exists(Paths.get(System.getProperty("user.dir")+"/images/emote/"+node.get("storeItemId") + ".png"))) {
                        String url = node.get("splashPath").asText();
                        byte[] imageBytes = callApi.callApiGetImage(url,
                                getGlobalTokenAndPort.getToken(),
                                getGlobalTokenAndPort.getPort(),
                                null);
                        String directoryPath = System.getProperty("user.dir")+"/images/emote/";
                        Files.createDirectories(Paths.get(directoryPath));
                        try (FileOutputStream fos = new FileOutputStream(directoryPath +node.get("storeItemId") + ".png")) {
                            fos.write(imageBytes);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        BufferedImage image = ImageIO.read(new File(directoryPath +node.get("storeItemId") + ".png"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }else{
                        BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir")+"/images/emote/"+node.get("storeItemId") + ".png"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }
                    recipesList.add(recipes);
                }
            }
            //猜测的小小英雄  待完善
            else if (message.equals("companion")){
                //图标
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
                    if (!Files.exists(Paths.get(System.getProperty("user.dir")+"/images/companion/"+node.get("storeItemId") + ".png"))) {
                        String url = node.get("splashPath").asText();
                        byte[] imageBytes = callApi.callApiGetImage(url,
                                getGlobalTokenAndPort.getToken(),
                                getGlobalTokenAndPort.getPort(),
                                null);
                        String directoryPath = System.getProperty("user.dir")+"/images/companion/";
                        Files.createDirectories(Paths.get(directoryPath));
                        try (FileOutputStream fos = new FileOutputStream(directoryPath +node.get("storeItemId") + ".png")) {
                            fos.write(imageBytes);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                        BufferedImage image = ImageIO.read(new File(directoryPath +node.get("storeItemId") + ".png"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }else{
                        BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir")+"/images/companion/"+node.get("storeItemId") + ".png"));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", baos);
                        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
                        recipes.setBase64Image(base64Image);
                    }
                    recipesList.add(recipes);
                }
            }

        }
        return recipesList;
    }

}
