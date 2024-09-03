package com.ywh.yxlmzs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


@SpringBootApplication
public class YxlmzsApplication {

    public static void main(String[] args) {
        SpringApplication.run(YxlmzsApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler http://localhost:8089/index.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
