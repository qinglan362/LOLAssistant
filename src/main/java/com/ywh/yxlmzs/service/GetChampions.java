package com.ywh.yxlmzs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class GetChampions {

    @Resource
    ObjectMapper objectMapper;

    public void getChampions() throws IOException {

        ClassPathResource resource = new ClassPathResource("static/versions.json");
        Path tempFile = Files.createTempFile("versions", ".json");
        Files.copy(resource.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        File file = new File(tempFile.toString());
        JsonNode versionNode = objectMapper.readTree(file);
        String version = versionNode.get("version").asText();

        ClassPathResource resource1 = new ClassPathResource("static/champion.json");
        Path tempFile1 = Files.createTempFile("champion", ".json");
        Files.copy(resource1.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        JsonResponse response = (JsonResponse) Unirest.get("http://ddragon.leagueoflegends.com/cdn/"+version+"/data/zh_CN/champion.json")
                    .asJson();
            String championPath = tempFile1.toString();
            try (FileWriter writer = new FileWriter(championPath)) {
                writer.write(response.getBody().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
