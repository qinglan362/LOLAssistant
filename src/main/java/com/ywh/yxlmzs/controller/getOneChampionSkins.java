package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.ChooseSkinFromChampionId;
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

@RestController
public class getOneChampionSkins {
    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper ob;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Autowired
    public getOneChampionSkins(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }
    @GetMapping("/getOneChampionSkin")
    public List<ChooseSkinFromChampionId> getSkins(@RequestParam Map<String,String> map) throws IOException {
       JsonNode jsonNode=ob.readTree(callApi.callApiGet(
                "/lol-game-data/assets/v1/champions/" + map.get("championId") + ".json",
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                null
        )).get("skins");

        System.out.println(jsonNode);

        String championId=map.get("championId");
        List<ChooseSkinFromChampionId> skinFromChampionIdList=new ArrayList<>();
        for (int i = 0; i < jsonNode.size(); i++) {
            //从0开始包含原画 从1开始不包含原画全是皮肤 但是无尽狂潮的英雄只有一个皮肤没有原画，或者说原画就是皮肤
            //无尽狂潮已经下线 不知道何时上线
            JsonNode skinNode=jsonNode.get(i);
            if (!Objects.isNull(skinNode.get("questSkinInfo"))){
                if (!Objects.isNull(skinNode.get("questSkinInfo").get("tiers"))){
                    JsonNode skinNode1=skinNode.get("questSkinInfo").get("tiers");
                    for (int j = 0; j < skinNode1.size(); j++) {
                        JsonNode skinNode2=skinNode1.get(j);
                        String skinId1=skinNode2.get("id").asText();
                        String name1=skinNode2.get("name").asText();
                        String path1=skinNode2.get("splashPath").asText();
                        String augments ="";
                        if (skinNode2.get("skinAugments")!=null){
                            if (skinNode2.get("skinAugments").get("augments")!=null){
                                augments=skinNode2.get("skinAugments").get("augments").get(0).get("contentId").asText();
                            }
                        }
                        String image1=getImage(path1,Integer.parseInt(skinId1));
                        skinFromChampionIdList.add(new ChooseSkinFromChampionId(championId,skinId1,image1,name1,augments));
                    }
                }
            }else if (!Objects.isNull(skinNode.get("skinAugments"))){
                   if (!Objects.isNull(skinNode.get("skinAugments").get("augments"))){
                       String skinId=skinNode.get("id").asText();
                       String name=skinNode.get("name").asText();
                       String path=skinNode.get("splashPath").asText();
                       String augments =skinNode.get("skinAugments").get
                               ("augments").get(0).get("contentId").asText();
                       String image=getImage(path,Integer.parseInt(skinId));
                       skinFromChampionIdList.add(new ChooseSkinFromChampionId(championId,skinId,image,name,augments));
                   }
            }
            else {
                String skinId=skinNode.get("id").asText();
                String name=skinNode.get("name").asText();
                String path=skinNode.get("splashPath").asText();
                String image=getImage(path,Integer.parseInt(skinId));
                System.out.println(image);
                System.out.println(skinId);
                System.out.println(name);
                System.out.println("!1111");
                skinFromChampionIdList.add(new ChooseSkinFromChampionId(championId,skinId,image,name,""));
            }
        }
        return skinFromChampionIdList;
    }
private String getImage(String path, Integer id) throws IOException {
    String filePath = System.getProperty("user.dir") + "/images/skin/" + id + ".jpg";

    if (!Files.exists(Paths.get(filePath))) {
        byte[] imageBytes = callApi.callApiGetImage(
                path,
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                null);

        String directoryPath = System.getProperty("user.dir") + "/images/skin/";
        Files.createDirectories(Paths.get(directoryPath));

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    return "skin/"+id + ".jpg";
}
}
