package me.taromati.streamerrecorder.api.twitcasting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
public class TwitcastingApi {
    private static final String baseDomain = "https://twitcasting.tv";
    private static final String apiEndpoint = baseDomain + "/streamserver.php";
    private static final String userAgent = "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36";

    public static String isLiveNGetLiveTitle(String streamer) {
        JsonNode info = getLiveInfo(streamer);
        if (info == null) {
            return null;
        }

        try {
            JsonNode idNode = info.path("movie").path("id");
            JsonNode liveNode = info.path("movie").path("live");
            if (liveNode.isMissingNode() || liveNode.isBoolean() == false) {
                log.trace("[TwitcastingApi] error checking stream online status");
                return null;
            }
            if (liveNode.asBoolean() == false) {
                log.trace("[TwitcastingApi] live stream is offline");
                return null;
            }

            String movieId = String.valueOf(idNode.asLong());
            String token = getToken(movieId, streamer);
            if (token == null) {
                return null;
            }
            return getTitle(movieId, token, streamer);
        } catch (Exception e) {
            return null;
        }
    }
    public static JsonNode getLiveInfo(String streamer) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            URI uri = new URI(apiEndpoint + "?target=" + streamer + "&mode=client");
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

    public static String getToken(String movieId, String streamer) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            URI uri = new URI("https://frontendapi.twitcasting.tv/movies/" + movieId + "/token");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("User-Agent", userAgent)
                    .header("Referer", baseDomain + "/" + streamer)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.trace("[TwitcastingApi] Live Check getToken Raw Response: {}, {}, {}", streamer, response.statusCode(), response.body());
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode tokenNode = mapper.readTree(response.body());
                return tokenNode.path("token").asText();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getTitle(String movieId, String token, String streamer) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            URI uri = new URI("https://frontendapi.twitcasting.tv/movies/" + movieId + "/status/viewer?token=" + token + "&hl=ko&__n=" + LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("User-Agent", userAgent)
                    .header("Referer", baseDomain + "/" + streamer)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            log.trace("[TwitcastingApi] Live Check getTitle Raw Response: {}, {}, {}", streamer, response.statusCode(), response.body());
            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode infoNode = mapper.readTree(response.body());
                return infoNode.path("movie").path("title").asText();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}

