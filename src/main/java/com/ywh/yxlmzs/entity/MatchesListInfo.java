package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchesListInfo {
    private String gameId;
    private Boolean win;
    private String championName;
    private String date;
    private String mapName;
    //斗魂竞技场名次
    private String placement;
}
