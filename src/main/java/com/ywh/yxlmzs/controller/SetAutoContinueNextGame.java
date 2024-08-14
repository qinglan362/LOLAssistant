package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.AutoContinueNextGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SetAutoContinueNextGame {


    private AutoContinueNextGame autoContinueNextGame;

    @Autowired
    public SetAutoContinueNextGame(AutoContinueNextGame autoContinueNextGame) {
        this.autoContinueNextGame = autoContinueNextGame;
    }

    @PostMapping("/setAutoContinueNextGame")
    public String setAutoContinueNextGame(@RequestParam  String autoContinueNext) {
        autoContinueNextGame.setAutoContinueNextGame(autoContinueNext);
        return "success";
    }
}
