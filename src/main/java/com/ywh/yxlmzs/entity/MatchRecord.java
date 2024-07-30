package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchRecord {
    private String gameId;
    private String gameName;
    private Integer deaths;
    private Integer kills;
    private Integer assists;
    private  String champLevel;
    private Boolean win;
    private  String championName;
    private String date;
    private String mapName;
    private Rank rank;
}
