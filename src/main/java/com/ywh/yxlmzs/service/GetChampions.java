package com.ywh.yxlmzs.service;

import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.io.*;
@Service
public class GetChampions {

    public void getChampions() {
        String versionFilePath = System.getProperty("user.dir") + "/version.txt";
        StringBuilder versionContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(versionFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                versionContent.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String version = versionContent.toString().trim();
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
