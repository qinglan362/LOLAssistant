package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class recipes {
    private String base64Image;
    private String disenchantValue;
    private String upgradeEssenceValue;
    private String value;
    private String count;
    private String storeItemId;
    private String championName;
    private String lootName;
    private String displayCategories;
    private Boolean isRental;
}
