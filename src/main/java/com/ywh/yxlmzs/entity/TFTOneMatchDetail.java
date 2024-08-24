package com.ywh.yxlmzs.entity;

import com.ywh.yxlmzs.entity.vo.ImageAndToolTips;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TFTOneMatchDetail {
    private String puuId;
    private String gameName;
    private List<ImageAndToolTips> traits;//羁绊
    private List<ImageAndToolTips> augments;//海克斯 augments
    private List<ImageAndMessage> units;//棋子
    private ImageAndToolTips companion;//小小英雄
    private String placement;//排名
    private TFTRank rank;//段位
    private String level;//等级
    private String rankImage;//对当前段位图片
}
