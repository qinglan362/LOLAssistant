package com.ywh.yxlmzs.entity.realEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class timeline {
    private Map<String, Double> creepsPerMinDeltas;
    private Map<String, Double> csDiffPerMinDeltas;
    private Map<String, Double> damageTakenDiffPerMinDeltas;
    private Map<String, Double> damageTakenPerMinDeltas;
    private Map<String, Double> goldPerMinDeltas;
    private String lane;
    private int participantId;
    private String role;
    private Map<String, Double> xpDiffPerMinDeltas;
    private Map<String, Double> xpPerMinDeltas;
}
