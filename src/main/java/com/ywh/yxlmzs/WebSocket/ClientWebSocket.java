package com.ywh.yxlmzs.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.utils.BanChampionId;
import com.ywh.yxlmzs.utils.CallApi;

import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import com.ywh.yxlmzs.utils.PickChampionId;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

@Component
public class ClientWebSocket extends TextWebSocketHandler {


    private WebSocketSession session;

    private GetGlobalTokenAndPort getGlobalTokenAndPort;

    private PickChampionId pickChampionId;
    private BanChampionId banChampionId;

    @Autowired
    public ClientWebSocket(GetGlobalTokenAndPort getGlobalTokenAndPort,PickChampionId pickChampionId,BanChampionId banChampionId) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
        this.pickChampionId=pickChampionId;
        this.banChampionId=banChampionId;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        CallApi callApi=new CallApi();
        String  port=getGlobalTokenAndPort.getPort();
        String  token=getGlobalTokenAndPort.getToken();
        ObjectMapper objectMapper=new ObjectMapper();
        System.out.println(objectMapper.readTree(message.getPayload()).toPrettyString());
        System.out.println("----------------------------------------------------------");
         if (message.getPayload().contains("Found")) {
            String Url= "/lol-matchmaking/v1/ready-check/accept";
            callApi.callApiPost(Url,token,port,null);
        }
                 if (message.getPayload().contains("/lol-champ-select/v1/session")){
                  JsonNode jsonNode=objectMapper.readTree(message.getPayload());
                  if (jsonNode.get(2).get("data").get("timer").get("phase").asText().equals("BAN_PICK")) {
                  JsonNode jsonNode1=jsonNode.get(2).get("data").get("actions");
                  int cellId=jsonNode.get(2).get("data").get("localPlayerCellId").asInt();
                  //actions是个数组，里面有很多个json对象，如何遍历
                      for (JsonNode subArray : jsonNode1) {
                          int id;
                          Map<String,Object> map;
                          for (JsonNode jsonNode2 : subArray) {
                              if (jsonNode2.get("actorCellId").asInt()==cellId&&jsonNode2.get("completed").asText().equals("false")){
                                  if (jsonNode2.get("type").asText().equals("pick")&&pickChampionId.getState().equals("start")) {
                                      map=Map.of(
                                              "actorCellId",cellId,
                                              "championId",pickChampionId.getChampionId(),
                                              "completed","true",
                                              "id",jsonNode2.get("id").asText(),
                                              "isAllyAction","true",
                                              "type","pick"
                                      );
                                      id=jsonNode2.get("id").asInt();
                                      callApi.callApiPatch("/lol-champ-select/v1/session/actions/" + id, token, port, map);
                                      break;
                                  }
                                  if (jsonNode2.get("type").asText().equals("ban")&&banChampionId.getState().equals("start")) {
                                      map=Map.of(
                                              "actorCellId",cellId,
                                              "championId",banChampionId.getChampionId(),
                                              "completed","true",
                                              "id",jsonNode2.get("id").asText(),
                                              "isAllyAction","true",
                                              "type","ban"
                                      );
                                      id=jsonNode2.get("id").asInt();
                                      callApi.callApiPatch("/lol-champ-select/v1/session/actions/" + id, token, port, map);
                                      break;
                                  }
                              }
                             }
                          }

//                  for (JsonNode jsonNode2:jsonNode1){
//                      if (jsonNode2.get("actorCellId").asInt()==cellId&&jsonNode2.get("completed").asText().equals("false")){
//                          if (jsonNode2.get("type").asText().equals("pick")&&pickChampionId.getState().equals("start")) {
//                              map=Map.of(
//                                      "actorCellId",cellId,
//                                      "championId",pickChampionId.getChampionId(),
//                                      "completed","true",
//                                      "id",jsonNode2.get("id").asText(),
//                                      "isAllyAction","true",
//                                      "type","pick"
//                              );
//                              id=jsonNode2.get("id").asInt();
//                              callApi.callApiPatch("/lol-champ-select/v1/session/actions/" + id, token, port, map);
//                              break;
//                          }
//                          if (jsonNode2.get("type").asText().equals("ban")&&banChampionId.getState().equals("start")) {
//                              map=Map.of(
//                                      "actorCellId",cellId,
//                                      "championId",banChampionId.getChampionId(),
//                                      "completed","true",
//                                      "id",jsonNode2.get("id").asText(),
//                                      "isAllyAction","true",
//                                      "type","ban"
//                              );
//                              id=jsonNode2.get("id").asInt();
//                              callApi.callApiPatch("/lol-champ-select/v1/session/actions/" + id, token, port, map);
//                              break;
//                          }
//                      }
//                  }
              }
        }


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
