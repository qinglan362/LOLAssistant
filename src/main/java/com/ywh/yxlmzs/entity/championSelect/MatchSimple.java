package com.ywh.yxlmzs.entity.championSelect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchSimple {
    private Integer kills;
    private Integer deaths;
    private Integer assists;
}
