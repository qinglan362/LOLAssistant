package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rank {
    private String RANKED_FLEX_SR_CurrentSeason_Division;
    private String RANKED_FLEX_SR_CurrentSeason_Tier;
    private String RANKED_FLEX_SR_CurrentSeason_HighestDivision;
    private String RANKED_FLEX_SR_CurrentSeason_HighestTier;
    private String RANKED_FLEX_SR_CurrentSeason_LeaguePoints;
    private String RANKED_FLEX_SR_PreviousSeasonHighestTier;

    private String RANKED_SOLO_5x5_CurrentSeason_Division;
    private String RANKED_SOLO_5x5_CurrentSeason_Tier;
    private String RANKED_SOLO_5x5_CurrentSeason_HighestDivision;
    private String RANKED_SOLO_5x5_CurrentSeason_HighestTier;
    private String RANKED_SOLO_5x5_CurrentSeason_LeaguePoints;
    private String RANKED_SOLO_5x5_PreviousSeasonHighestTier;
}
