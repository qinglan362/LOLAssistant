package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChooseSkinFromChampionId {
    private String championId;
    private String skinId;
    private String image;
    private String name;
    private String augments;
}
