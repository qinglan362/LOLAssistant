package com.ywh.yxlmzs.utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.mapsInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetMaps {

    @Resource
    CallApi callApi;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Resource
    ObjectMapper objectMapper;

    public void getMaps() throws IOException {
        String url = "/lol-game-queues/v1/queues";
        String token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
        String port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
        String Maps = callApi.callApiGet(url, token, port, null);
        try {
            JsonNode rootNode = objectMapper.readTree(Maps);
            Path filePath = Paths.get("src/main/resources/static/maps.json");
            File file = filePath.toFile();
            if (file.exists()) {
                Files.delete(filePath);
            }
            List<mapsInfo> mapInfoList = new ArrayList<>();
            for (JsonNode node : rootNode) {
                String availability = node.get("queueAvailability").asText();
                if ("Available".equals(availability)) {
                    String name = node.get("name").asText();
                    String mapId = node.get("mapId").asText();
                    String id = node.get("id").asText();
                    String gameMode = node.get("gameMode").asText();
                    mapsInfo mapInfo = new mapsInfo(name, mapId, id, gameMode);
                    mapInfoList.add(mapInfo);
                }
            }
            objectMapper.writeValue(file, mapInfoList);
            System.out.println("maps.json文件已生成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
