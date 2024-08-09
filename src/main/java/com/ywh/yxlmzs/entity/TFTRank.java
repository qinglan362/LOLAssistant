package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TFTRank {
    private String RANKED_TFT_CurrentSeason_Division;
    private String RANKED_TFT_CurrentSeason_Tier;
    private String RANKED_TFT_CurrentSeason_HighestDivision;
    private String RANKED_TFT_CurrentSeason_HighestTier;
    private String RANKED_TFT_CurrentSeason_LeaguePoints;
    private String RANKED_TFT_PreviousSeasonHighestTier;

    private String RANKED_TFT_DOUBLE_UP_CurrentSeason_Division;
    private String RANKED_TFT_DOUBLE_UP_CurrentSeason_Tier;
    private String RANKED_TFT_DOUBLE_UP_CurrentSeason_HighestDivision;
    private String RANKED_TFT_DOUBLE_UP_CurrentSeason_HighestTier;
    private String RANKED_TFT_DOUBLE_UP_CurrentSeason_LeaguePoints;
    private String RANKED_TFT_DOUBLE_UP_PreviousSeasonHighestTier;

    private String RANKED_TFT_TURBO_CurrentSeason_Division;
    private String RANKED_TFT_TURBO_CurrentSeason_Tier;
    private String RANKED_TFT_TURBO_CurrentSeason_HighestDivision;
    private String RANKED_TFT_TURBO_CurrentSeason_HighestTier;
    private String RANKED_TFT_TURBO_CurrentSeason_LeaguePoints;
    private String RANKED_TFT_TURBO_PreviousSeasonHighestTier;
}
