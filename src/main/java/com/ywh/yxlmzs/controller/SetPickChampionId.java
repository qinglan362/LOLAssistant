package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.PickChampionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SetPickChampionId {

    private PickChampionId pickChampionId;

    @Autowired
    public SetPickChampionId(PickChampionId pickChampionId) {
        this.pickChampionId = pickChampionId;
    }

    @PostMapping("/setPickChampion")
    public void setPickChampionId(@RequestParam Map<String,String> pickChampion) {
        pickChampionId.setChampionId(Integer.parseInt(pickChampion.get("championId")));
        pickChampionId.setState(pickChampion.get("state"));
        System.out.println("Set championId to " + pickChampion);
    }
}
