package com.ywh.yxlmzs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetAccessToken {

    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public GetAccessToken(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }


    public String getAccessToken() throws JsonProcessingException {

        String token=getGlobalTokenAndPort.getToken();
        String port=getGlobalTokenAndPort.getPort();
        JsonNode jsonNode = objectMapper.readTree(
                callApi.callApiGet(
                        "/entitlements/v1/token",
                         token,
                         port,
                         null
                )
        );
        return jsonNode.get("accessToken").asText();
    }
}
