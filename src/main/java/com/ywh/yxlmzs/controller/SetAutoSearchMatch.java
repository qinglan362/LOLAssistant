package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.AutoSearchMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SetAutoSearchMatch {

    private AutoSearchMatch autoSearchMatch;

    @Autowired
    public SetAutoSearchMatch(AutoSearchMatch autoSearchMatch) {
        this.autoSearchMatch = autoSearchMatch;
    }

    @PostMapping("/autoSearchMatch")
    public String setAutoSearchMatch(@RequestParam Map<String,String> map) {
        autoSearchMatch.setState(map.get("setAutoSearchMatch"));
        autoSearchMatch.setFirstPosition(map.get("firstPosition"));
        autoSearchMatch.setSecondPosition(map.get("secondPosition"));
        if (map.get("firstPosition").equals(map.get("secondPosition"))) {
          return "首副选位置不可一样";
        }
        return "success";
    }
}
