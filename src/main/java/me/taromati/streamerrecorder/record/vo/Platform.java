package me.taromati.streamerrecorder.record.vo;

public enum Platform {
    CHZZK,
    SOOP,
    TWITCASTING,
    TWITCH,
//    YOUTUBE,
    ;

    public static Platform from(String name) {
        for (Platform platform : Platform.values()) {
            if (platform.name().equals(name)) {
                return platform;
            }
        }
        throw new IllegalArgumentException(STR."Unexpected platform value: \{name}");
    }
}
