package com.ywh.yxlmzs.service;

import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class GetSummoners {

    @Resource
    CallApi callApi;

    @Resource
    GetGlobalTokenAndPort getGlobalTokenAndPort;

    public String getSummoners(Map<String,Object> map){
        String port=getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
        String token=getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
        String url="/lol-summoner/v1/summoners";
        return callApi.callApiGet(url,token,port,map);
    }
}
