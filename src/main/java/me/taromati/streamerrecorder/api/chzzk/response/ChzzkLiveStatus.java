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
        private Boolean krOnlyViewing;
        private String openDate;
        private String closeDate;
        private Boolean clipActive;
        private String chatChannelId;
        private List<String> tags;
        private String categoryType;
        private String liveCategory;
        private String liveCategoryValue;
        private String livePollingStatusJson;
        private String faultStatus;
        private String userAdultStatus;
        private Boolean abroadCountry;
        private String blindType;
        private String playerRecommendContent;
        private Boolean chatActive;
        private String chatAvailableGroup;
        private String chatAvailableCondition;
        private Integer minFollowerMinute;
        private Boolean allowSubscriberInFollowerMode;
        private Integer chatSlowModeSec;
        private Boolean chatEmojiMode;
        private Boolean chatDonationRankingExposure;
        private String dropsCampaignNo;
        private List<String> liveTokenList;
        private String watchPartyNo;
        private String watchPartyTag;
        private Boolean timeMachineActive;
        private String channelId;
        private String lastAdultReleaseDate;
        private String lastKrOnlyViewingReleaseDate;
    }
}
