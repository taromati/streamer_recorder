package me.taromati.streamerrecorder.api.chzzk.response;

import lombok.Data;

@Data
public class ChzzkAccessToken {
    private String code;
    private String message;
    private Content content;

    @Data
    public static class Content {
        private String accessToken;
        private TemporaryRestrict temporaryRestrict;
        private Boolean realNameAuth;
        private String extraToken;

        @Data
        public static class TemporaryRestrict {
            private Boolean temporaryRestrict;
            private Integer times;
            private Object duration;
            private Object createdTime;
        }
    }
}
