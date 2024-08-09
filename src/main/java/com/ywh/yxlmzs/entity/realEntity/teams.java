package com.ywh.yxlmzs.entity.realEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class teams {
    private List<Object> bans;
    private int baronKills;
    private int dominionVictoryScore;
    private int dragonKills;
    private boolean firstBaron;
    private boolean firstBlood;
    private boolean firstDargon;
    private boolean firstInhibitor;
    private boolean firstTower;
    private int hordeKills;
    private int inhibitorKills;
    private int riftHeraldKills;
    private int teamId;
    private int towerKills;
    private int vilemawKills;
    private String win;
}
