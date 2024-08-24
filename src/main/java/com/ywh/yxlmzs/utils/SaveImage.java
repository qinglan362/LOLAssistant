package com.ywh.yxlmzs.utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.type.IterationType;
import com.ywh.yxlmzs.entity.vo.ImageAndToolTips;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class SaveImage {

    @Resource
    CallApi callApi;
    private final GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public SaveImage(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    public List<ImageAndToolTips> saveImage(JsonNode node, List<String> ids, String folderName,  String extension, String type) throws IOException {

        List<ImageAndToolTips> imageAndToolTipsList = new ArrayList<>();

        int count=0;
        String url="";

        Set<String> idSet = new HashSet<>(ids);
        List<JsonNode> matchingNodes = new ArrayList<>();

        label:
        for (JsonNode js : node) {
            switch (type) {
                case "companion" -> {
                    if (idSet.contains(js.get("contentId").asText())) {
                        matchingNodes.add(js);
                        count++;
                    }
                    if (count == idSet.size()) {
                        break label;
                    }
                }
                case "items", "cherry-augments", "summoner-spells", "champion-summary", "perks", "profile-icons" -> {
                    if (idSet.contains(js.get("id").asText())) {
                        matchingNodes.add(js);
                        count++;
                    }
                    if (count == idSet.size()) {
                        break label;
                    }
                }
                case "tftChampions" -> {
                    if (idSet.contains(js.get("character_record").get("character_id").asText())) {
                        matchingNodes.add(js);
                        count++;
                    }
                    if (count == idSet.size()) {
                        break label;
                    }
                }
                case "tftitems" -> {
                    if (idSet.contains(js.get("nameId").asText())) {
                        matchingNodes.add(js);
                        count++;
                    }
                    if (count == idSet.size()) {
                        break label;
                    }
                }
                case "tfttraits" -> {
                    if (idSet.contains(js.get("trait_id").asText())) {
                        matchingNodes.add(js);
                        count++;
                    }
                    if (count == idSet.size()) {
                        break label;
                    }
                }
            }
        }

        for (JsonNode js:matchingNodes){
                    ImageAndToolTips imageAndToolTips = new ImageAndToolTips();
                    String filePath = switch (type) {
                        case "items", "cherry-augments", "summoner-spells", "champion-summary", "perks",
                             "profile-icons" ->
                                System.getProperty("user.dir") + "/images/" + folderName + "/" + js.get("id").asText() + "." + extension;
                        case "companion" ->
                                System.getProperty("user.dir") + "/images/" + folderName + "/" + js.get("contentId").asText() + "." + extension;
                        case "tftChampions" ->
                                System.getProperty("user.dir") + "/images/" + folderName + "/" + js.get("character_record").get("character_id").asText() + "." + extension;
                        case "tftitems" ->
                                System.getProperty("user.dir") + "/images/" + folderName + "/" + js.get("nameId").asText() + "." + extension;
                        case "tfttraits" ->
                                System.getProperty("user.dir") + "/images/" + folderName + "/" + js.get("trait_id").asText() + "." + extension;
                        default -> "";
                    };
                    //只取filePath的folderName以及后面的部分
                   imageAndToolTips.setImage(filePath.substring(filePath.indexOf(folderName)));
                    imageAndToolTips.setToolTips("");
            switch (type) {
                case "champion-summary" -> {
                    url = js.get("squarePortraitPath").asText();
                    imageAndToolTips.setToolTips(js.get("name").asText());
                }
                case "items", "summoner-spells", "perks", "profile-icons" -> {
                    url = js.get("iconPath").asText();
                    if (!Objects.isNull(js.get("name"))) {
                        imageAndToolTips.setToolTips(js.get("name").asText());
                    }
                }
                case "cherry-augments" -> {
                    url = js.get("augmentSmallIconPath").asText();
                    imageAndToolTips.setToolTips(js.get("nameTRA").asText());
                }
                case "companion" -> {
                    url = js.get("loadoutsIcon").asText();
                    imageAndToolTips.setToolTips(js.get("name").asText());
                }
                case "tftChampions" -> {
                    url = js.get("character_record").get("squareIconPath").asText();
                    imageAndToolTips.setToolTips(js.get("character_record").get("display_name").asText());
                }
                case "tftitems" -> {
                    url = js.get("squareIconPath").asText();
                    imageAndToolTips.setToolTips(js.get("name").asText());
                }
                case "tfttraits" -> {
                    url = js.get("icon_path").asText();
                    imageAndToolTips.setToolTips(js.get("tooltip_text").asText());
                }
            }
                    if (!Files.exists(Paths.get(filePath))) {
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
                    imageAndToolTipsList.add(imageAndToolTips);
        }

        return imageAndToolTipsList;
    }
}
