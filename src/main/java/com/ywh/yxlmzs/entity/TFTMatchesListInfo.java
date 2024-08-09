package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TFTMatchesListInfo {
    private String gameId;
    private String date;
    private String placement;
    private String puuId;
    private List<String> tags;
}
