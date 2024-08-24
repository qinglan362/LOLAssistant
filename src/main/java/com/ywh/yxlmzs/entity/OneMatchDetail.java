package com.ywh.yxlmzs.entity;

import com.ywh.yxlmzs.entity.vo.ImageAndToolTips;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OneMatchDetail {
    private MatchesListInfo matchesListInfo;
    private Rank rank;
    private String gameName;
    private Integer deaths;
    private Integer kills;
    private Integer assists;
    private String champLevel;
    private List<ImageAndToolTips> itemsImage;
    private List<ImageAndToolTips> spellsImage;
    private ImageAndToolTips championImage;
    private String rankImage;
    private List<ImageAndToolTips> perkImage;
    private  String totalDamageTaken;
    private  String totalDamageDealtToChampions;
    private  String wardsPlaced;
    private String totalMinionsKilled;
    //斗魂竞技场名次1-8
    private String subteamPlacement;
    //斗魂竞技场海克斯
    private List<ImageAndToolTips> augments;
    //斗魂竞技场分数
    private String ratedRating;
}
