package com.ywh.yxlmzs.service;

import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class GetGameFromGameId {

    @Resource
    CallApi callApi;
    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;

    public String getGameFromGameId(String gameId) {
        String url = "/lol-match-history/v1/games/" + gameId;
        String token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
        String port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
        return callApi.callApiGet(url, token, port, null);
    }
}
