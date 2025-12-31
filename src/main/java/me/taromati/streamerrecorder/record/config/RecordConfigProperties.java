package me.taromati.streamerrecorder.record.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="record")
public class RecordConfigProperties {
    private String fileDir;
    private Boolean useTransformMp4;
    private Boolean deleteTs;
    private Soop soop;
    private Chzzk chzzk;

    @Getter
    @Setter
    public static class Soop {
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class Chzzk {
        private String NID_AUT;
        private String NID_SES;
    }
}
