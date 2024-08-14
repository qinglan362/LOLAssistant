package com.ywh.yxlmzs.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SetRank {
    @Resource
    CallApi callApi;
    private GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Autowired
    public SetRank(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @PostMapping("/setRank")
    public String setRank(@RequestParam Map<String,String> map) {
        JSONObject body = new JSONObject(1);
        JSONObject jsonObject = new JSONObject(3);
        jsonObject.put("rankedLeagueQueue",map.get("rankedLeagueQueue"));
        jsonObject.put("rankedLeagueTier", map.get("rankedLeagueTier"));
        if (map.get("rankedLeagueDivision") != null){
            jsonObject.put("rankedLeagueDivision", map.get("rankedLeagueDivision"));
        }else {
            jsonObject.put("rankedLeagueDivision", "I");
        }
        body.put("lol", jsonObject);
        callApi.callApiPut(
                "/lol-chat/v1/me",
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                body
        );
        return "设置成功";
    }
}
