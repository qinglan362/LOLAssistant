package com.ywh.yxlmzs.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

@Service
public class GetEventHelp {
    @Resource
    CallApi callApi;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;
    public void getEventHelp() {
        String port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
        String token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
        String url = "/help";
        String Auth="riot:"+token;
        String cmlvdD= Base64.getEncoder().encodeToString(Auth.getBytes());
        String projectRoot = System.getProperty("user.dir");
        JsonResponse response = (JsonResponse) Unirest.get("https://127.0.0.1:"+port+url)
                .header("Authorization", "Basic "+cmlvdD)
                .asJson();
        String championPath = projectRoot + "/src/main/resources/static/help.json";
        try (FileWriter writer = new FileWriter(championPath)) {
            writer.write(response.getBody().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
