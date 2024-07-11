package com.ywh.yxlmzs.utils;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class GetGlobalTokenAndPort {

    public Map<String,String> GlobalTokenAndPortSet() {
                Map<String,String> GlobalTokenAndPort = new HashMap<>();
                String token = null;
                String port = null;
                // 获取当前工作目录并构建文件路径
                String filePath = Paths.get(System.getProperty("user.dir"), "tokenAndPort.txt").toString();
                try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("Token:")) {
                            token = line.split("Token:")[1].trim();
                            GlobalTokenAndPort.put("Token", token);
                        } else if (line.startsWith("Port:")) {
                            port = line.split("Port:")[1].trim();
                            GlobalTokenAndPort.put("Port", port);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return GlobalTokenAndPort;
    }
}
