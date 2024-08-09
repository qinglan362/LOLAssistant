package com.ywh.yxlmzs.entity.realEntity.up;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class games {
    private String gameBeginDate;
    private int gameCount;
    private String gameEndDate;
    private int gameIndexBegin;
    private int gameIndexEnd;
    private List<com.ywh.yxlmzs.entity.realEntity.games> games;
}
