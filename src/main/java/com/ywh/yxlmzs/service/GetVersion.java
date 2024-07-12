package com.ywh.yxlmzs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.Version;
import jakarta.annotation.Resource;
import kong.unirest.JsonNode;
import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class GetVersion {

    @Resource
    ObjectMapper objectMapper;

    public void getVersions() {
        JsonResponse response = (JsonResponse) Unirest.get("https://ddragon.leagueoflegends.com/api/versions.json")
                .asJson();
        JsonNode body = response.getBody();
        String version = body.getArray().get(0).toString();

        String projectRoot = System.getProperty("user.dir");
        // 构建version.txt文件路径
        File versionFile = new File(projectRoot, "version.txt");

        try {
            // 如果文件存在，则删除
            Path filePath = Paths.get("src/main/resources/static/versions.json");
            File file = filePath.toFile();
            if (file.exists()) {
                Files.delete(filePath);
            }
            // 创建新的文件并写入数据
            Version versionInfo = new Version(version);

            // 将对象列表写入JSON文件
            objectMapper.writeValue(file, versionInfo);
            System.out.println("versions.json文件已生成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
