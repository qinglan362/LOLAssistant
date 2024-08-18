package com.ywh.yxlmzs.entity;


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
    private String icon;
}
