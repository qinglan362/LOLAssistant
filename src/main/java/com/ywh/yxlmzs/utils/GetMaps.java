//package com.ywh.yxlmzs.utils;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.Resource;
//import org.springframework.stereotype.Service;
//import com.fasterxml.jackson.databind.JsonNode;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//
//@Service
//public class GetMaps {
//
//    @Resource
//    CallApi callApi;
//    @Resource
//    GetGlobalTokenAndPort getGlobalTokenAndPort;
//    @Resource
//    ObjectMapper objectMapper;
//
//    public void getMaps() {
//        String projectRoot = System.getProperty("user.dir");
//        String url = "/lol-game-queues/v1/queues";
//        String token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
//        String port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
//        String Maps = callApi.callApiGet(url, token, port, null);
//        try {
//            JsonNode rootNode = objectMapper.readTree(Maps);
//            BufferedWriter writer = new BufferedWriter(new FileWriter("maps.txt"));
//            for (JsonNode node : rootNode) {
//                String availability = node.get("queueAvailability").asText();
//                if ("Available".equals(availability)) {
//                    System.out.println(node);
//                    String name = node.get("name").asText();
//                    String mapId = node.get("mapId").asText();
//                    String id = node.get("id").asText();
//                    String gameMode= node.get("gameMode").asText();
//                    writer.write("name: " + name);
//                    writer.newLine();
//                    writer.write("mapId: " + mapId);
//                    writer.newLine();
//                    writer.write("id: " + id);
//                    writer.newLine();
//                    writer.write("gameMode: " + gameMode);
//                    writer.newLine();
//                    writer.newLine();  // Add an empty line between groups
//                }
//            }
//
//            writer.close();
//            System.out.println("maps.txt文件已生成");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
package com.ywh.yxlmzs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.mapsInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GetMaps {

    @Resource
    CallApi callApi;

    @Resource
    ObjectMapper objectMapper;

    private AllMaps allMaps;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public GetMaps(GetGlobalTokenAndPort getGlobalTokenAndPort, AllMaps allMaps) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.allMaps = allMaps;
    }

    public void getMaps() throws IOException {
        String url = "/lol-game-queues/v1/queues";
        String token = getGlobalTokenAndPort.getToken();
        String port = getGlobalTokenAndPort.getPort();
        String Maps = callApi.callApiGet(url, token, port, null);
        try {
            JsonNode rootNode = objectMapper.readTree(Maps);
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
            allMaps.setList(mapInfoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
