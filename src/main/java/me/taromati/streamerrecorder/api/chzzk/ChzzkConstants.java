package me.taromati.streamerrecorder.api.chzzk;

import lombok.Getter;

public class ChzzkConstants {

    @Getter
    public enum LiveStatus {
        OPEN("OPEN"),
        CLOSE("CLOSE"),
        ;

        private final String value;

        LiveStatus(String value) {
            this.value = value;
        }
    }
}
