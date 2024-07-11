package com.ywh.yxlmzs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.Version;
import com.ywh.yxlmzs.entity.mapsInfo;
import jakarta.annotation.Resource;
import kong.unirest.JsonNode;
import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.io.File;
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

        try {
            Path filePath = Paths.get("src/main/resources/static/versions.json");
            File file = filePath.toFile();

            // 如果文件存在，删除它
            if (file.exists()) {
                Files.delete(filePath);
            }

            // 创建新的文件并写入数据
            Version versionInfo = new Version(version);

            // 将对象列表写入JSON文件
            objectMapper.writeValue(file, versionInfo);
            System.out.println("versions.json文件已生成");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
