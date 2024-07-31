package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.PickChampionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SetPickChampionId {

    private final PickChampionId pickChampionId;

    @Autowired
    public SetPickChampionId(PickChampionId pickChampionId) {
        this.pickChampionId = pickChampionId;
    }

    @PostMapping("/setPickChampion")
    public void setPickChampionId(@RequestParam String championId) {
        pickChampionId.setChampionId(Integer.parseInt(championId));
        System.out.println("Set championId to " + championId);
    }
}
