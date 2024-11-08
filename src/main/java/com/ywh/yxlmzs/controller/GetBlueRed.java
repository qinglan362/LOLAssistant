package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetBlueRed {

    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;

    @GetMapping("/getBlueRed")
    public String getBlueRed() throws JsonProcessingException {
        String token = getGlobalTokenAndPort.getToken();
        String port = getGlobalTokenAndPort.getPort();
        JsonNode s=objectMapper.readTree(callApi.callApiGet("/lol-champ-select/v1/pin-drop-notification",
                token,
                port,
                null
        ));
        String side=s.get("mapSide").asText();
      return side;
    }
}
