package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.vo.ImageAndToolTips;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class GetOwnXuancai {

    @Resource
    CallApi callApi;

    @Resource
    ObjectMapper objectMapper;

    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;

    @GetMapping("/getOwnXuancai")
    public List<ImageAndToolTips> getOwnXuancai(@RequestParam String championId) throws IOException {
        String summonerId =objectMapper.readTree(callApi.callApiGet(
                "/lol-summoner/v1/current-summoner",
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                null
        )).get("summonerId").asText();
        JsonNode allSkinChromas=objectMapper.readTree(callApi.callApiGet(
               "/lol-champions/v1/inventories/"+summonerId+"/champions/"+championId+"/skins",
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                null
        ));
        List<ImageAndToolTips> skinChromas=new ArrayList<>();
        System.out.println(allSkinChromas);
        for (JsonNode skinChroma:allSkinChromas) {
            if (skinChroma.get("ownership").get("owned").asBoolean()){
                if (!skinChroma.get("chromas").isEmpty()){
                   for (JsonNode chroma:skinChroma.get("chromas")){
                       if (chroma.get("ownership").get("owned").asBoolean()){
                           saveImages("png",chroma.get("chromaPath").asText(),chroma.get("id").asText());
                           skinChromas.add(new ImageAndToolTips(("skinChromas/"+chroma.get("id").asText()+".png"),skinChroma.get("name").asText()));
                       }
                   }
                }
            }
        }
        return skinChromas;
    }

    public void saveImages(  String extension,String url,String id) throws IOException {
           String filePath=System.getProperty("user.dir") + "/images/" + "skinChromas" + "/" + id + "." + extension;
                if (!Files.exists(Paths.get(filePath))) {
                    byte[] imageBytes = callApi.callApiGetImage(
                            url,
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null);
                    String directoryPath = System.getProperty("user.dir") + "/images/" + "skinChromas" + "/";
                    Files.createDirectories(Paths.get(directoryPath));
                    try (FileOutputStream fos = new FileOutputStream(filePath)) {
                        fos.write(imageBytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
             }
    }
}
