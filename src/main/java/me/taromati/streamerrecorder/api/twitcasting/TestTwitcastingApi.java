package me.taromati.streamerrecorder.api.twitcasting;

import com.fasterxml.jackson.databind.JsonNode;

public class TestTwitcastingApi {
    public static void main(String[] args) {
        String streamer = "prnt_tina"; // 실제로 테스트할 스트리머의 ID로 바꿔주세요.

//        System.out.println("Testing getLiveInfo:");
//        JsonNode liveInfo = TwitcastingApi.getLiveInfo(streamer);
//        System.out.println(liveInfo != null ? liveInfo.toString() : "null");
//
//        String movieId = liveInfo != null && liveInfo.has("movie") && liveInfo.get("movie").has("id") ? liveInfo.get("movie").get("id").asText() : "";
//        if (!movieId.isEmpty()) {
//            System.out.println("\nTesting getToken:");
//            String token = TwitcastingApi.getToken(movieId, streamer);
//            System.out.println(token != null ? token : "null");
//
//            if (token != null) {
//                System.out.println("\nTesting getTitle:");
//                String title = TwitcastingApi.getTitle(movieId, token, streamer);
//                System.out.println(title != null ? title : "null");
//            }
//        }

        String title = TwitcastingApi.isLiveNGetLiveTitle(streamer);
        System.out.println("\nTesting isLiveNGetLiveTitle:");
        System.out.println(title != null ? title : "null");
    }
}