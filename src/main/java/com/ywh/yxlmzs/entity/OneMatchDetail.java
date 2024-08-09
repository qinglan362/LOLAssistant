package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneMatchDetail {
    private MatchesListInfo matchesListInfo;
    private Rank rank;
    private String gameName;
    private Integer deaths;
    private Integer kills;
    private Integer assists;
    private String champLevel;
    private List<String> itemsImage;
    private List<String> spellsImage;
    private String championImage;
    private String rankImage;
    private List<String> perkImage;
    private  String totalDamageTaken;
    private  String totalDamageDealtToChampions;
    private  String wardsPlaced;
    private String totalMinionsKilled;
    //斗魂竞技场名次1-8
    private String subteamPlacement;
}
