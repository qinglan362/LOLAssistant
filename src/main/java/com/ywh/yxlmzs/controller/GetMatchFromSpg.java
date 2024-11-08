package com.ywh.yxlmzs.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywh.yxlmzs.service.GetAccessToken;
import com.ywh.yxlmzs.utils.CallApi;
import com.ywh.yxlmzs.utils.GetGlobalTokenAndPort;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetMatchFromSpg {

    private final GetGlobalTokenAndPort getGlobalTokenAndPort;
    @Resource
    GetAccessToken getAccessToken;
    @Resource
    CallApi callApi;
    @Resource
    ObjectMapper objectMapper;

    @Autowired
    public GetMatchFromSpg(GetGlobalTokenAndPort getGlobalTokenAndPort) {
        this.getGlobalTokenAndPort = getGlobalTokenAndPort;
    }

    @GetMapping("/getMatchFromSpg")
    public void getMatchFromSpg(@RequestParam Map<String,String> map) throws JsonProcessingException {
        String regin=getGlobalTokenAndPort.getRegin().toLowerCase();
        String token1=getAccessToken.getAccessToken();
        String puuid= objectMapper.readTree(callApi.callApiGetSpg(
                "https://prod-rso.lol.qq.com:3001/aliases/v1/aliases?gameName="+map.get("gameName")+"&tagLine="+map.get("tagLine"),
                token1,
                null
        )).get(0).get("puuid").asText();

            String endpoint = "/match-history-query/v1/products/lol/player/" + puuid + "/SUMMARY?startIndex=" + 0 + "&count=" + 10;
            JSONObject jsonObject = null;
            String resp=callApi.callApiGetSpg(
                    buildUrl(regin)+endpoint,
                    token1,
                    null
            );
            if (resp.equals("[]")) {

            } else {
                jsonObject = JSONObject.parseObject(resp);
            }
         System.out.println( JSON.parseArray(jsonObject.get("games").toString()));
      }
      public String buildUrl(String region) {
        String s = region.toLowerCase();
        if (s.equals("hn1") || s.equals("hn10") || s.equals("bgp2")) {
            return "https://" + s + "-k8s-sgp.lol.qq.com:21019";
        }
        return "https://" + s + "-sgp.lol.qq.com:21019";
    }
}
//    /**
//     * 通过玩家puuid查询近几把战绩
//     *
//     * @param id         玩家信息中的puuid
//     * @param startIndex 起始局数
//     * @param count      结尾局数
//     */
//    public List<SpgProductsMatchHistoryBO> getProductsMatchHistoryByPuuid(String region, String id, int startIndex, int count) throws IOException {
//        String endpoint = "/match-history-query/v1/products/lol/player/" + id + "/SUMMARY?startIndex=" + startIndex + "&count=" + count;
//        JSONObject jsonObject = null;
//        String resp = requestSpgUtil.doGet(endpoint, region);
//        if (resp.equals("[]")) {
//            return null;
//        } else {
//            jsonObject = JSONObject.parseObject(resp);
//
//        }
//        return JSON.parseArray(jsonObject.get("games").toString(), SpgProductsMatchHistoryBO.class);
//    }
//
//
//
//    public String doGet(String endpoint) throws IOException {
//        Request request = new Request.Builder()
//                .url(defaultUrl + endpoint)
//                .get()
//                .build();
//        return this.callString(request);
//    }
//
//    public String doGet(String endpoint, String region) throws IOException {
//        Request request = new Request.Builder()
//                .url(buildUrl(region) + endpoint)
//                .get()
//                .build();
//        return this.callString(request);
//    }
//    public final static List<String> REGION = new ArrayList<String>() {
//        {
//            add("tj100");
//            add("hn1");
//            add("cq100");
//            add("gz100");
//            add("nj100");
//            add("hn10");
//            add("tj101");
//            add("bgp2");
//        }
//    };

