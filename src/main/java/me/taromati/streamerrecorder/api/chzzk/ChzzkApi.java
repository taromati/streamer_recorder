package me.taromati.streamerrecorder.api.chzzk;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.api.chzzk.response.ChzzkAccessToken;
import me.taromati.streamerrecorder.api.chzzk.response.ChzzkLiveStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

@Slf4j
public class ChzzkApi {
    public static ObjectMapper objectMapper = new ObjectMapper();

    public static String isLiveNGetLiveTitle(String id) {
        ChzzkLiveStatus chzzkLiveStatus = getLiveStatus(id);
        if (chzzkLiveStatus == null || chzzkLiveStatus.getContent() == null) {
            return null;
        }
        if (Objects.equals(chzzkLiveStatus.getContent().getStatus(), ChzzkConstants.LiveStatus.OPEN.getValue())) {
            return chzzkLiveStatus.getContent().getLiveTitle();
        } else {
            return null;
        }
    }
    public static String getChatChannelId(String id) {
        ChzzkLiveStatus chzzkLiveStatus = getLiveStatus(id);
        if (chzzkLiveStatus == null || chzzkLiveStatus.getContent() == null) {
            return null;
        }
        return chzzkLiveStatus.getContent().getChatChannelId();
    }

    // TODO: Cookie를 이용한 API 호출
    public static String getAccessToken(String chatChannelId) {
        String requestURL = "https://comm-api.game.naver.com/nng_main/v1/chats/access-token?channelId=" + chatChannelId + "&chatType=STREAMING";

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(requestURL))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .build(); // HttpRequest 생성

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                ChzzkAccessToken data = objectMapper.readValue(response.body(), ChzzkAccessToken.class);
                String accessToken = data.getContent().getAccessToken();
                String extraToken = data.getContent().getExtraToken();
                return STR."\{accessToken};\{extraToken}";
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    public static ChzzkLiveStatus getLiveStatus(String id) {
        String requestURL = STR."https://api.chzzk.naver.com/polling/v2/channels/\{id}/live-status";

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .uri(URI.create(requestURL))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .build(); // HttpRequest 생성


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.trace("[ChzzkApi] Live Check Raw Response : {}, {}, {}", id, response.statusCode(), response.body());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), ChzzkLiveStatus.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("[ChzzkApi] Live Check Error: {}, {}", id, e.getMessage());
            return null;
        }
    }
}
