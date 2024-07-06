package com.ywh.yxlmzs.utils;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
public class  CallApi {
     public String callApiGet(String url, String token, String port, Map<String,Object> params) {
         String Auth="riot:"+token;
         String cmlvdD= Base64.getEncoder().encodeToString(Auth.getBytes());
         HttpResponse<String> response = Unirest.get("https://127.0.0.1:"+port+url)
                 .queryString(params)
                 .header("Authorization", "Basic "+cmlvdD)
                 .asString();
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
}
