package com.ywh.yxlmzs.service;
import com.ywh.yxlmzs.utils.Version;
import kong.unirest.JsonNode;
import kong.unirest.JsonResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GetVersion {

    private Version globalVersion;

    @Autowired
    public GetVersion(Version version) {
        this.globalVersion = version;
    }

    public void getVersions() {
        JsonResponse response = (JsonResponse) Unirest.get("https://ddragon.leagueoflegends.com/api/versions.json")
                .asJson();
        JsonNode body = response.getBody();
        String version = body.getArray().get(0).toString();
        globalVersion.setVersion(version);
    }
}
