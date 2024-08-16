package com.ywh.yxlmzs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.Champion;
import com.ywh.yxlmzs.utils.AllChampions;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import com.ywh.yxlmzs.utils.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class GetChampions {



        private final Version  globalVersion;
        private final AllChampions allChampions;
        private final GetGlobalTokenAndPort getGlobalTokenAndPort;
        @Autowired
        public GetChampions(Version version, AllChampions allChampions, GetGlobalTokenAndPort getGlobalTokenAndPort) {
            this.globalVersion = version;
            this.allChampions = allChampions;
            this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        }

        public void getChampions() throws JsonProcessingException {
            CallApi callApi = new CallApi();
            List<Champion> list = new ArrayList<>();
            String version = globalVersion.getVersion();
            ObjectMapper objectMapper = new ObjectMapper();

           JsonNode champion=objectMapper.readTree(
                    callApi.callApiGet(
                            "/lol-game-data/assets/v1/champion-summary.json",
                            getGlobalTokenAndPort.getToken(),
                            getGlobalTokenAndPort.getPort(),
                            null
                    )
            );
           for (JsonNode cn:champion) {
             list.add(new Champion(cn.get("name").asText(), cn.get("id").asInt()));
           }
           allChampions.setList(list);
    }
}
