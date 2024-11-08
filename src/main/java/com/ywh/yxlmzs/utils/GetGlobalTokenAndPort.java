package com.ywh.yxlmzs.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class GetGlobalTokenAndPort {
    private String token;
    private  String port;
    private String regin;
}
