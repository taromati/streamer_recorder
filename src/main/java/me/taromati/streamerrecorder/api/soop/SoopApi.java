package me.taromati.streamerrecorder.api.soop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.api.soop.response.SoopLiveInfo;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class SoopApi {

    public static String isLiveNGetLiveTitle(String bjid) {
        SoopLiveInfo soopLiveInfo = getPlayerLive(bjid);
        if (soopLiveInfo == null) {
            return null;
        }
        if (soopLiveInfo.CHDOMAIN() != null && soopLiveInfo.CHPT() != null) {
            return soopLiveInfo.TITLE();
        } else {
            return null;
        }
    }

    public static SoopLiveInfo getPlayerLive(String bjid) {
        String requestURL = String.format("https://live.sooplive.co.kr/afreeca/player_live_api.php?bjid=%s", bjid);

        try (HttpClient client = HttpClient.newHttpClient()) {
            Map<String, String> bodyJson = Maps.newHashMap();
            bodyJson.put("bid", bjid);
            bodyJson.put("bno", "null");
            bodyJson.put("type", "live");
            bodyJson.put("pwd", "");
            bodyJson.put("player_type", "html5");
            bodyJson.put("stream_type", "common");
            bodyJson.put("quality", "HD");
            bodyJson.put("mode", "landing");
            bodyJson.put("is_revive", "false");
            bodyJson.put("from_api", "0");

            HttpRequest request = HttpRequest.newBuilder()
                    .POST(ofFormData(bodyJson))
                    .uri(URI.create(requestURL))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build(); // HttpRequest 생성


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.trace("[SoopApi] Live Check Raw Response : {}, {}, {}", bjid, response.statusCode(), response.body());

            if (response.statusCode() == 200) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonObject = objectMapper.readTree(response.body());
                JsonNode channel = jsonObject.get("CHANNEL");

                return new SoopLiveInfo(
                        channel.get("CHDOMAIN").asText(),
                        channel.get("CHATNO").asText(),
                        channel.get("FTK").asText(),
                        channel.get("TITLE").asText(),
                        channel.get("BJID").asText(),
                        channel.get("BNO").asText(),
                        channel.get("CHIP").asText(),
                        String.valueOf(Integer.parseInt(channel.get("CHPT").asText()) + 1),
                        channel.get("CTIP").asText(),
                        channel.get("CTPT").asText(),
                        channel.get("GWIP").asText(),
                        channel.get("GWPT").asText()
                );
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static HttpRequest.BodyPublisher ofFormData(Map<String, String> data) {
        var builder = new StringBuilder();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (builder.isEmpty() == false) {
                builder.append("&");
            }

            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
