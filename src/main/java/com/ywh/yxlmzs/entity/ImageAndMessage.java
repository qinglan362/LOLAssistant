package com.ywh.yxlmzs.entity;

import com.ywh.yxlmzs.entity.vo.ImageAndToolTips;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageAndMessage {
    private ImageAndToolTips unitImage;
    private List<ImageAndToolTips> itemImage;
}
