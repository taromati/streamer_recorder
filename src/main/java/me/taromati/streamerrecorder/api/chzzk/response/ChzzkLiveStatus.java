package me.taromati.streamerrecorder.api.chzzk.response;

import lombok.Data;

import java.util.List;

@Data
public class ChzzkLiveStatus {
    private String code;
    private String message;
    private Content content;

    @Data
    public static class Content {
        private String liveTitle;
        private String status;
        private Integer concurrentUserCount;
        private Integer accumulateCount;
        private Boolean paidPromotion;
        private Boolean adult;
        private Boolean clipActive;
        private String chatChannelId;
        private List<String> tags;
        private String categoryType;
        private String liveCategory;
        private String liveCategoryValue;
        private String livePollingStatusJson;
        private String faultStatus;
        private String userAdultStatus;
        private Boolean chatActive;
        private String chatAvailableGroup;
        private String chatAvailableCondition;
        private Integer minFollowerMinute;
        private Boolean chatDonationRankingExposure;
    }
}
