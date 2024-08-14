package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdateOnlineState {
    @Resource
    CallApi callApi;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;

    @PostMapping("/updateOnlineState")
    public String updateOnlineState(@RequestParam String state)   {
      callApi.callApiPut("/lol-chat/v1/me", getGlobalTokenAndPort.getToken(), getGlobalTokenAndPort.getPort(), Map.of("availability", state));
        return "success";
    }
}
