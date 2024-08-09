package com.ywh.yxlmzs.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class SaveImage {

    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;
    private final GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public SaveImage(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    public String saveImage(String floderName, String fileName, String houzhui,String type ) throws IOException {

        if (!Files.exists(Paths.get(System.getProperty("user.dir")+"/images/"+floderName+"/"+fileName+ "."+houzhui))) {
            JsonNode typeImage  = objectMapper.readTree(
                    callApi.callApiGet(
                            "/lol-game-data/assets/v1/"+type+".json",
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null
                    )
            );
            String url="";
            for (JsonNode ti : typeImage) {
                if (ti.get("id").asInt() == Integer.parseInt(fileName)) {
                    if (type.equals("champion-summary")) {
                        url = ti.get("squarePortraitPath").asText();
                    } else if (type.equals("items")||type.equals("summoner-spells")||type.equals("perks")) {
                        url = ti.get("iconPath").asText();
                    }
                    break;
                }
            }
            byte[] imageBytes = callApi.callApiGetImage(url,
                    getGlobalTokenAndPort.getToken(),
                    getGlobalTokenAndPort.getPort(),
                    null);
            String directoryPath = System.getProperty("user.dir")+"/images/"+floderName+"/";
            Files.createDirectories(Paths.get(directoryPath));
            try (FileOutputStream fos = new FileOutputStream(directoryPath +fileName + "."+houzhui)) {
                fos.write(imageBytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            BufferedImage image = ImageIO.read(new File(directoryPath +fileName + "."+houzhui));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, houzhui, baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            if (fileName.equals("119")) {
                System.out.println("Dsadasb+"+base64Image);
            }
           return  base64Image;
        }else{
            BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir")+"/images/"+floderName+"/"+fileName + "."+houzhui));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, houzhui, baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
            return base64Image;
        }
    }

}
