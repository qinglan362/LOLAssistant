package com.ywh.yxlmzs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.entity.recipes;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class DistantLootRecipes {
    @Resource
    CallApi callApi;
    @Resource
    GetGlobalTokenAndPort  getGlobalTokenAndPort;
    @Resource
    ObjectMapper ob;

    @PostMapping("/distant")
    public void distantLootRecipes(@RequestBody List<recipes> recipesList) throws JsonProcessingException {
         //差小小英雄 图标 表情
        //System.out.println(recipesList);
        for (recipes recipe : recipesList) {
            String[] playerLootList = {recipe.getLootName()};
            String recipeName="";
            String type=recipe.getDisplayCategories();
            if (type.equals("SKIN")) {
                if (recipe.getIsRental()) {
                    recipeName = "SKIN_RENTAL_disenchant";
                } else {
                    recipeName = "SKIN_disenchant";
                }
            }else if(type.equals("CHAMPION")){
                if (recipe.getIsRental()) {
                    recipeName = "CHAMPION_RENTAL_disenchant";
                }else{
                    recipeName="CHAMPION_disenchant";
                }
            }else if(type.equals("WARDSKIN")){
                recipeName="WARDSKIN_disenchant";
            }else if(type.equals("WARDSKIN_RENTAL")){
                recipeName="WARDSKIN_RENTAL_disenchant";
            }
            else if(type.equals("STATSTONE_SHARD")){
                recipeName="STATSTONE_SHARD_DISENCHANT";
            }else if(type.equals("STATSTONE")){
                recipeName="STATSTONE_DISENCHANT";
            }else if(type.equals("SUMMONERICON")){
                if (recipe.getIsRental()) {
                    recipeName = "SUMMONERICON_RENTAL_disenchant";
                }else{
                    recipeName="SUMMONERICON_disenchant";
                }
            }else if(type.equals("EMOTE")){
                if (recipe.getIsRental()) {
                    recipeName = "EMOTE_RENTAL_disenchant";
                }else{
                    recipeName="EMOTE_disenchant";
                }
            }else if(type.equals("COMPANION")){
                if (recipe.getIsRental()) {
                    recipeName = "CHAMPION_RENTAL_disenchant";
                }else{
                    recipeName="CHAMPION_disenchant";
                }
            }
            String url="/lol-loot/v1/recipes/"+recipeName+"/craft?repeat="+Integer.parseInt(recipe.getCount());
            callApi.callApiPostObject(url,
                    getGlobalTokenAndPort.getToken(),
                    getGlobalTokenAndPort.getPort(),
                    playerLootList
            );
        }
    }
}
