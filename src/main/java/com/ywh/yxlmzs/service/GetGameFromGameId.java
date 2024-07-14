package com.ywh.yxlmzs.service;

import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GetGameFromGameId {

    @Resource
    CallApi callApi;
//    @Resource
//    GetGlobalTokenAndPort getGlobalTokenAndPort;
    private GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public GetGameFromGameId(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    public String getGameFromGameId(String gameId) throws IOException {
        String url = "/lol-match-history/v1/games/" + gameId;
        String token = getGlobalTokenAndPort.getToken();
        String port = getGlobalTokenAndPort.getPort();
        return callApi.callApiGet(url, token, port, null);
    }
}
