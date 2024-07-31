package com.ywh.yxlmzs.service;

import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GetSummoners {

    @Resource
    CallApi callApi;

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;

    @Autowired
    public GetSummoners(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    public String getSummoners(Map<String,Object> map) {
        String port=getGlobalTokenAndPort.getPort();
        String token=getGlobalTokenAndPort.getToken();
        String url="/lol-summoner/v1/summoners";
        return callApi.callApiGet(url,token,port,map);
    }
}
