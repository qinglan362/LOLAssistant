package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.service.GetSummoners;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UpdateStateMessage {

    @Resource
    CallApi callApi;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Autowired
    public UpdateStateMessage(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @PostMapping("/updateStateMessage")
    public String updateStateMessage(@RequestParam String message) {
        callApi.callApiPut("/lol-chat/v1/me",  getGlobalTokenAndPort.getToken(), getGlobalTokenAndPort.getPort(), Map.of("statusMessage", message));
        return "success";
    }
}
