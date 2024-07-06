package com.ywh.yxlmzs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedWriter;
import java.io.FileWriter;

@Service
public class GetMaps {

    @Resource
    CallApi callApi;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Resource
    ObjectMapper objectMapper;

    public void getMaps() {
        String url = "/lol-game-queues/v1/queues";
        String token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
        String port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
        String Maps = callApi.callApiGet(url, token, port, null);
        try {
            JsonNode rootNode = objectMapper.readTree(Maps);
            BufferedWriter writer = new BufferedWriter(new FileWriter("maps.txt"));
            for (JsonNode node : rootNode) {
                String availability = node.get("queueAvailability").asText();
                if ("Available".equals(availability)) {
                    String name = node.get("name").asText();
                    String mapId = node.get("mapId").asText();
                    String id = node.get("id").asText();

                    writer.write("name: " + name);
                    writer.newLine();
                    writer.write("mapId: " + mapId);
                    writer.newLine();
                    writer.write("id: " + id);
                    writer.newLine();
                    writer.newLine();  // Add an empty line between groups
                }
            }

            writer.close();
            System.out.println("maps.txt文件已生成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
