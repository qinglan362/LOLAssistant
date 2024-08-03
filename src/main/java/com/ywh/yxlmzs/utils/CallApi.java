package com.ywh.yxlmzs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
public class  CallApi {

    @Resource
    ObjectMapper objectMapper;

     public String callApiGet(String url, String token, String port, Map<String,Object> params) {
         String Auth="riot:"+token;
         String cmlvdD= Base64.getEncoder().encodeToString(Auth.getBytes());
         HttpResponse<String> response = Unirest.get("https://127.0.0.1:"+port+url)
                 .queryString(params)
                 .header("Authorization", "Basic "+cmlvdD)
                 .asString();
         return response.getBody();
    }


    public byte[] callApiGetImage(String url, String token, String port, Map<String,Object> params) throws JsonProcessingException {
        String Auth="riot:"+token;
        String cmlvdD= Base64.getEncoder().encodeToString(Auth.getBytes());
        HttpResponse<byte[]> response = Unirest.get("https://127.0.0.1:"+port+url)
                .queryString(params)
                .header("Connection", "keep-alive")
                .header("Accept", "*/*")
                .header("Accept", "image/jpeg")
                .header("Authorization", "Basic "+cmlvdD)
                .asBytes();
        return response.getBody();
    }
    public String callApiPost(String url, String token, String port, Map<String,Object> params) {
        String Auth="riot:"+token;
        String cmlvdD= Base64.getEncoder().encodeToString(Auth.getBytes());
        HttpResponse<String> response = Unirest.post("https://127.0.0.1:"+port+url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic "+cmlvdD)
                .body(params)
                .asString();
        return response.getBody();
    }
    public String callApiPostObject(String url, String token, String port, Object params) {
        String Auth="riot:"+token;
        String cmlvdD= Base64.getEncoder().encodeToString(Auth.getBytes());
        HttpResponse<String> response = Unirest.post("https://127.0.0.1:"+port+url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic "+cmlvdD)
                .body(params)
                .asString();
        return response.getBody();
    }

    public void callApiPatch(String url, String token, String port, Map<String,Object> params) {
        String Auth="riot:"+token;
        String cmlvdD= Base64.getEncoder().encodeToString(Auth.getBytes());
        HttpResponse<String> response = Unirest.patch("https://127.0.0.1:"+port+url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic "+cmlvdD)
                .body(params)
                .asString();
        response.getBody();
    }
    public String callApiPut(String url, String token, String port, Map<String,Object> params) {
        String Auth="riot:"+token;
        String cmlvdD= Base64.getEncoder().encodeToString(Auth.getBytes());
        HttpResponse<String> response = Unirest.put("https://127.0.0.1:"+port+url)
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic "+cmlvdD)
                .body(params)
                .asString();
        return response.getBody();
     }
}
