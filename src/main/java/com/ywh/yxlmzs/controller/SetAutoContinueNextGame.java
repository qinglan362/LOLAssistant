package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.AutoContinueNextGame;
import com.ywh.yxlmzs.utils.CallApi;
import jakarta.annotation.Resource;
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
        System.out.println(autoContinueNextGame.getAutoContinueNextGame());
        return "success";
    }
}
