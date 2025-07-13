package me.taromati.streamerrecorder.api.twitcasting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class TwitcastingApi {
    private static final String baseDomain = "https://twitcasting.tv";
    private static final String apiEndpoint = baseDomain + "/streamserver.php";
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36";

    public static boolean isLive(String streamer) throws Exception {
        JsonNode info = getLiveInfo(streamer);
        if (info == null) {
            return false;
        }

        try {
            JsonNode liveNode = info.path("movie").path("live");
            if (liveNode.isMissingNode() || liveNode.isBoolean() == false) {
                log.trace("[TwitcastingApi] error checking stream online status");
                return false;
            }
            if (liveNode.asBoolean() == false) {
                log.trace("[TwitcastingApi] live stream is offline");
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static JsonNode getLiveInfo(String streamer) throws Exception {
        URI uri = new URI(apiEndpoint + "?target=" + streamer + "&mode=client");
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("User-Agent", userAgent)
                    .header("Referer", baseDomain + "/" + streamer)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.trace("[TwitcastingApi] Live Check Raw Response: {}, {}, {}", streamer, response.statusCode(), response.body());
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readTree(response.body());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}

