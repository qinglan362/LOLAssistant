package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.entity.Champion;
import com.ywh.yxlmzs.utils.AllChampions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VueGetChampions {

    private AllChampions allChampions;

    @Autowired
    public VueGetChampions(AllChampions allChampions) {
        this.allChampions = allChampions;
    }

    @GetMapping("/getChampions")
    public List<Champion> getChampions() {
       return allChampions.getList();
    }
}
