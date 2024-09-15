package com.ywh.yxlmzs.entity.championSelect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnePersonHistory {
    private String name;//召唤师id
    List<MatchSimple> oneHistory;
}
