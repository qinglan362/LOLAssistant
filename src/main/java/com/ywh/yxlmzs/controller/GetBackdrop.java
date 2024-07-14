package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.service.GetSummoners;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class GetBackdrop {

    @Resource
    CallApi callApi;
    @Resource
    GetSummoners getSummoners;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Autowired
    public GetBackdrop(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @GetMapping("/getBackdrop")
    public String getBackdrop(@RequestParam Map<String,Object> map) throws IOException {
        ObjectMapper objectMapper=new ObjectMapper();
        String summonerInfo=getSummoners.getSummoners(map);
        JsonNode jsonNode= objectMapper.readTree(summonerInfo);
        String url="/lol-collections/v1/inventories/"+jsonNode.get("summonerId").asText()+"/backdrop";
        String port=getGlobalTokenAndPort.getPort();
        String token=getGlobalTokenAndPort.getToken();
        JsonNode jsonNode1=objectMapper.readTree(callApi.callApiGet(url,token,port,null));
        String backdropImage=jsonNode1.get("backdropImage").asText();
        String[] backdropImageArray=backdropImage.split("/");
        String championName=backdropImageArray[7].substring(4);
        if(championName.charAt(0)=='0'){
            championName=championName.substring(1);
        }
        return "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/"+backdropImageArray[5]+"_"+championName+".jpg";
    }
}