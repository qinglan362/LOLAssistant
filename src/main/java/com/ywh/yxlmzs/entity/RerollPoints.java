package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RerollPoints {

    private int currentPoints;
    private int maxRolls;
    private int numberOfRolls;
    private int pointsCostToRoll;
    private int pointsToReroll;
}
