package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.SendTeamMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutoSendTeamMatch {

    private SendTeamMatch sendTeamMatch;

    @Autowired
    public AutoSendTeamMatch(SendTeamMatch sendTeamMatch) {
        this.sendTeamMatch = sendTeamMatch;
    }

    @PostMapping("/sendTeamMatch")
    public String autoSendTeamMatch(@RequestParam String setAutoSendTeamMatch) {
        sendTeamMatch.setState(setAutoSendTeamMatch);
        System.out.println("AutoSendTeamMatch state: " + sendTeamMatch.getState());
        return "success";
    }
}
