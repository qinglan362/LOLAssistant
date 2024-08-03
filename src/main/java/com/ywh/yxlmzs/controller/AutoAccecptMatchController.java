package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.AutoAccecptMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutoAccecptMatchController {

    private AutoAccecptMatch autoAccecptMatch;

    @Autowired
    public AutoAccecptMatchController(AutoAccecptMatch autoAccecpt) {
        this.autoAccecptMatch = autoAccecpt;
    }

    @PostMapping("/autoAccecptMatch")
    public String autoAccecptMatch(@RequestParam String isAutoAccecptMatch) {
        autoAccecptMatch.setIsAutoAccecptMatch(isAutoAccecptMatch);
        System.out.println("set autoAccecptMatch to " + autoAccecptMatch.getIsAutoAccecptMatch());
        return "success";
    }
}
