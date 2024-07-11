package com.ywh.yxlmzs.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;

@Service
public class GetGlobalTokenAndPort {

    public Map<String,String> GlobalTokenAndPortSet() throws IOException {
                Map<String,String> GlobalTokenAndPort = new HashMap<>();
                 ObjectMapper objectMapper = new ObjectMapper();
                  File file=new File("src/main/resources/static/tokenAndPort.json");
                   JsonNode jsonNode = objectMapper.readTree(file);
                    GlobalTokenAndPort.put("Token",jsonNode.get("Token").asText());
                    GlobalTokenAndPort.put("Port",jsonNode.get("Port").asText());
                    return GlobalTokenAndPort;
    }
}
