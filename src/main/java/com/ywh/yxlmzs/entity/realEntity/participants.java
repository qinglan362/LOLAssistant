package com.ywh.yxlmzs.entity.realEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class participants {
    private int championId;
    private String highestAchievedSeasonTier;
    private int participantId;
    private int spell1Id;
    private int spell2Id;
    private stats stats;
    private int teamId;
    private timeline timeline;

}
