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

    /**
     * 设置rank段位,默认设置为单排最强王者1
     * <p>
     * 段位:
     * value="IRON">坚韧黑铁
     * value="BRONZE">英勇黄铜
     * value="SILVER">不屈白银
     * value="GOLD">荣耀黄金
     * value="PLATINUM">华贵铂金
     * value="EMERALD">流光翡翠
     * value="DIAMOND">璀璨钻石
     * value="MASTER">超凡大师
     * value="GRANDMASTER">傲世宗师
     * value="CHALLENGER">最强王者
     * value="UNRANKED">没有段位
     * 段位级别
     * value="IV"
     * value="III"
     * value="II"
     * value="I"
     * rank模式
     * value="RANKED_SOLO_5x5">单排/双排
     * value="RANKED_FLEX_SR">灵活组排 5v5
     * value="RANKED_FLEX_TT">灵活组排 3v3
     * value="RANKED_TFT">云顶之弈
     */
    @PostMapping("/setRank")
    public String setRank(@RequestParam Map<String,String> map) {
        System.out.println(map);
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
