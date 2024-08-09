package com.ywh.yxlmzs.entity.realEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class player {
    private long accountId;
    private long currentAccountId;
    private String currentPlatformId;
    private String gameName;
    private String matchHistoryUri;
    private String platformId;
    private int profileIcon;
    private String puuid;
    private long summonerId;
    private String summonerName;
    private String tagLine;
}
