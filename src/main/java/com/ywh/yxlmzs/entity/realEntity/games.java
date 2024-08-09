package com.ywh.yxlmzs.entity.realEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class games {
    private String endOfGameResult;
    private long gameCreation;
    private Date gameCreationDate;
    private int gameDuration;
    private long gameId;
    private String gameMode;
    private String gameType;
    private String gameVersion;
    private int mapId;
    private List<participantIdentities> participantIdentities;
    private List<participants> participants;
    private String platformId;
    private int queueId;
    private int seasonId;
    private List<teams> teams;
}
