package com.ywh.yxlmzs.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchRecord {
    private String gameName;
    private Integer deaths;
    private Integer kills;
    private Integer assists;
    private  String champLevel;
    private Boolean win;
    private  String championName;
}