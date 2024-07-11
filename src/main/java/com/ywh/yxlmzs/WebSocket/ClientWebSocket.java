package com.ywh.yxlmzs.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientWebSocket extends TextWebSocketHandler {

   private WebSocketSession session;

   public List<String> summonerIds = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        CallApi callApi=new CallApi();
        GetGlobalTokenAndPort getGlobalTokenAndPort=new GetGlobalTokenAndPort();
         if (message.getPayload().contains("OnJsonApiEvent_lol-lobby_v2_lobby")){
          if (message.getPayload().contains("Found")) {
              String Url= "/lol-matchmaking/v1/ready-check/accept";
              String  port = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Port");
              String  token = getGlobalTokenAndPort.GlobalTokenAndPortSet().get("Token");
              callApi.callApiPost(Url,token,port,null);
          }
          if (message.getPayload().contains("Searching")){
              System.out.println("Searching for a match");
          }
        }
        System.out.println("Received message: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 处理连接关闭事件
        System.out.println("Connection closed: " + session.getId());
    }

    public void subscribe(String subscribeMessage) throws Exception {
        // 订阅
        session.sendMessage(new TextMessage(subscribeMessage));
        System.out.println("订阅成功: " + subscribeMessage);
    }

    public void unsubscribe(String unsubscribeMessage) throws Exception {
        //取消订阅
        session.sendMessage(new TextMessage(unsubscribeMessage));
        System.out.println("取消订阅成功: " + unsubscribeMessage);
    }
}
