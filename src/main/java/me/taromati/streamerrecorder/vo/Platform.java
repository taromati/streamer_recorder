package me.taromati.streamerrecorder.vo;

public enum Platform {
    TWITCH,
    CHZZK,
    AFREECA,
    YOUTUBE,
    TWITCASTING,
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
