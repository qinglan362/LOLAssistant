package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckNameAvailability {
       @Resource
       CallApi callApi;
       @Resource
      ObjectMapper ob;

      private GetGlobalTokenAndPort getGlobalTokenAndPort;
         @Autowired
        public CheckNameAvailability(GetGlobalTokenAndPort getGlobalTokenAndPort) {
            this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        }
        @GetMapping("/checkNameAvailability")
       public String checkNameAvailability(@RequestParam String name) throws JsonProcessingException {
         return callApi.callApiGet(
                    "/lol-summoner/v1/check-name-availability-new-summoners/"+name,
                    getGlobalTokenAndPort.getToken(),
                    getGlobalTokenAndPort.getPort(),
                    null
            );
    }
}
