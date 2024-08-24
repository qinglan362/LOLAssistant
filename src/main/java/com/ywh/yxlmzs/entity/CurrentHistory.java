package com.ywh.yxlmzs.entity;


import com.ywh.yxlmzs.entity.vo.ImageAndToolTips;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentHistory {
    private List<MatchRecord> matchRecords;
    private String rank;
    private String name;
    private ImageAndToolTips icon;
    //斗魂竞技场分数
    private String rate;
}
