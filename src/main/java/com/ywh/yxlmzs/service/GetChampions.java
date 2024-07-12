package com.ywh.yxlmzs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.io.*;
@Service
public class GetChampions {


        @Resource
        ObjectMapper objectMapper;

        public void getChampions() throws IOException {
            String versionFilePath = System.getProperty("user.dir") + "/src/main/resources/static/versions.json";
            File file = new File(versionFilePath);
            JsonNode versionNode = objectMapper.readTree(file);
            String version = versionNode.get("version").asText();
        String projectRoot = System.getProperty("user.dir");
        JsonResponse response = (JsonResponse) Unirest.get("http://ddragon.leagueoflegends.com/cdn/"+version+"/data/zh_CN/champion.json")
                    .asJson();
            String championPath = projectRoot + "/src/main/resources/static/champion.json";
            try (FileWriter writer = new FileWriter(championPath)) {
                writer.write(response.getBody().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
