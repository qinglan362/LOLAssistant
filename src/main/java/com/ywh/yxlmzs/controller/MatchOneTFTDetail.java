package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchOneTFTDetail {
    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public MatchOneTFTDetail(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @GetMapping("/TFTMatchOneDetail")
    public String TFTMatchOneDetail(@RequestParam String gameId) throws JsonProcessingException {
        //TFT国服好像没有可以根据gameId查询某一局的信息的接口
        ///lol-match-history/v1/games/"+gameId,这个接口是可以查询到某一局的信息的，但是TFT不好用，暂时搁置改为一次查询十次所有信息
//        System.out.println(gameId);
//        JsonNode gameDetail = objectMapper.readTree(callApi.callApiGet(
//                "/lol-match-history/v1/games/"+gameId,
//                getGlobalTokenAndPort.getToken(),
//                getGlobalTokenAndPort.getPort(),
//                null
//        ));
//        System.out.println(gameDetail.toPrettyString());

        return null;
    }
}
