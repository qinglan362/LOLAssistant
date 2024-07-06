package com.ywh.yxlmzs.utils;

import com.ywh.yxlmzs.api.service.GetChampions;
import com.ywh.yxlmzs.api.service.GetVersion;
import jakarta.annotation.Resource;
import kong.unirest.Unirest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    @Resource
    GetTokenAndPort getTokenAndPort;
    @Resource
    GetMaps getMaps;
    @Resource
    GetVersion getVersion;
    @Resource
    GetChampions getChampions;
    @Override
    public void run(String... args) throws Exception {
        Unirest.config().verifySsl(false);
        getTokenAndPort.getPortAndToken();
        getMaps.getMaps();
        getVersion.getVersions();
        getChampions.getChampions();
    }
}
