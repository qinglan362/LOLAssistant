package com.ywh.yxlmzs.controller;

import com.ywh.yxlmzs.utils.AutoSwap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutoSwapPosition{

    private AutoSwap autoSwap;

    @Autowired
    public AutoSwapPosition(AutoSwap autoSwap) {
        this.autoSwap = autoSwap;
    }

    @PostMapping("/autoSwap")
    public void autoSwap(@RequestParam String state) {
        autoSwap.setState(state);
        System.out.println("AutoSwap state: " + autoSwap.getState());
    }
}
