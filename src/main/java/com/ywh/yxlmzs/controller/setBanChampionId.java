package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.BanChampionId;
import com.ywh.yxlmzs.utils.PickChampionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class setBanChampionId {
    private BanChampionId banChampionId;

    @Autowired
    public setBanChampionId(BanChampionId banChampionId) {
        this.banChampionId = banChampionId;
    }

    @PostMapping("/setBanChampion")
    public void SetBanChampion(@RequestParam Map<String,String> pickChampion) {
        banChampionId.setChampionId(Integer.parseInt(pickChampion.get("championId")));
        banChampionId.setState(pickChampion.get("state"));
        System.out.println("Set BanChampionId to " + banChampionId);
    }
}
