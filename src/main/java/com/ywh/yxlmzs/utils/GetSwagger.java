package com.ywh.yxlmzs.utils;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GetSwagger {

           @Resource
            CallApi callApi;
            @Resource
            GetGlobalTokenAndPort getGlobalTokenAndPort;

        public void getSwagger() throws IOException {
            String port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
            String token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
            String url = "/swagger/v2/swagger.json";
            System.out.println( callApi.callApiGet(url, token, port, null));
        }

        public void getOpenApi() throws IOException {
            String port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
            String token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
            String url = "/swagger/v3/openapi.json";
            System.out.println( callApi.callApiGet(url, token, port, null));
        }
}
