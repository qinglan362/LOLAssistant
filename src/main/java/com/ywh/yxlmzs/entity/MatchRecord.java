package com.ywh.yxlmzs.entity;

import com.ywh.yxlmzs.entity.vo.ImageAndToolTips;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchRecord {
    private String gameId;
    private Integer deaths;
    private Integer kills;
    private Integer assists;
    private String champLevel;
    private Boolean win;
    private String championName;
    private ImageAndToolTips championIcon;
    private String date;
    private String mapName;
}
