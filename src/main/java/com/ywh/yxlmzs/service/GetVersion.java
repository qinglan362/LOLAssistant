package com.ywh.yxlmzs.service;

import kong.unirest.JsonNode;
import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


@Service
public class GetVersion {
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
            if (versionFile.exists()) {
                versionFile.delete();
            }
            // 创建新的version.txt文件
            versionFile.createNewFile();

            // 写入版本信息
            try (FileWriter writer = new FileWriter(versionFile)) {
                writer.write(version);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
