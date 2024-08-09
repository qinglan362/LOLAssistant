package com.ywh.yxlmzs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageAndMessage {
    private String unitImage;
    private List<String> itemImage;
}
