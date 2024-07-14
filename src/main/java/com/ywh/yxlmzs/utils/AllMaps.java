package com.ywh.yxlmzs.utils;

import com.ywh.yxlmzs.entity.mapsInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
public class AllMaps {
    private List<mapsInfo> list;
}
