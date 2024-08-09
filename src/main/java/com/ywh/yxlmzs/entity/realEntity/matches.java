package com.ywh.yxlmzs.entity.realEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class matches {
    private long accountId;
    private com.ywh.yxlmzs.entity.realEntity.up.games games;
    private String platformId;
}
