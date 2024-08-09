package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TFT {
    private List<TFTOneMatchDetail> TFTOneMatchDetail;
    private String date;
    private String gameId;
    private List<String> tags;
}
