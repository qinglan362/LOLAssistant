package com.ywh.yxlmzs.utils;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AutoSearchMatch {
    private String state;
    private String firstPosition;
    private String secondPosition;
}
