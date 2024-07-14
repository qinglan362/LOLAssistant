package com.ywh.yxlmzs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.Champion;
import com.ywh.yxlmzs.utils.AllChampions;
import com.ywh.yxlmzs.utils.Version;
import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class GetChampions {

        private Version  globalVersion;
        private AllChampions allChampions;
        @Autowired
        public GetChampions(Version version, AllChampions allChampions) {
            this.globalVersion = version;
            this.allChampions = allChampions;
        }

        public void getChampions() throws JsonProcessingException {
            List<Champion> list = new ArrayList<>();
            String version = globalVersion.getVersion();
           JsonResponse response = (JsonResponse) Unirest.get("http://ddragon.leagueoflegends.com/cdn/"+version+"/data/zh_CN/champion.json")
                    .asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode=objectMapper.readTree(response.getBody().toString()).get("data");
           for (JsonNode node:jsonNode) {
               list.add(new Champion(node.get("name").asText(),node.get("key").asInt()));
           }
           allChampions.setList(list);
    }
}
