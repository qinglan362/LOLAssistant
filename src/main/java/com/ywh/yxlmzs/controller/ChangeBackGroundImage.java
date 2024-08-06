package com.ywh.yxlmzs.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ywh.yxlmzs.entity.ChooseSkinFromChampionId;
import com.ywh.yxlmzs.entity.recipes;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChangeBackGroundImage {
    @Resource
    CallApi callApi;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public ChangeBackGroundImage(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @PostMapping("/changeBackGroundImage")
    public String changeBackGroundImage(@RequestBody List<ChooseSkinFromChampionId> chooseSkinFromChampionIds) {
        JSONObject body = new JSONObject(2);
		body.put("key", "backgroundSkinId");
		body.put("value", chooseSkinFromChampionIds.get(0).getSkinId());
        callApi.callApiPostObject(
                "/lol-summoner/v1/current-summoner/summoner-profile",
                getGlobalTokenAndPort.getToken(),
                getGlobalTokenAndPort.getPort(),
                body
        );
        //皮肤增强
        if(chooseSkinFromChampionIds.get(0).getAugments() != null){
            JSONObject body1 = new JSONObject(2);
            body1.put("key", "backgroundSkinAugments");
            body1.put("value",chooseSkinFromChampionIds.get(0).getAugments());
            callApi.callApiPostObject(
                    "/lol-summoner/v1/current-summoner/summoner-profile",
                    getGlobalTokenAndPort.getToken(),
                    getGlobalTokenAndPort.getPort(),
                    body1
            );
        }
        return "success";
    }
}
