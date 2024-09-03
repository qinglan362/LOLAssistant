package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentInfoB {
    private long accountId;
    private String displayName;
    private String gameName;
    private String internalName;
    private boolean nameChangeFlag;
    private int percentCompleteForNextLevel;
    private String privacy;
    private String profileIconId;
    private String puuid;
    private RerollPoints rerollPoints;
    private long summonerId;
    private int summonerLevel;
    private String tagLine;
    private boolean unnamed;
    private int xpSinceLastLevel;
    private int xpUntilNextLevel;
}
