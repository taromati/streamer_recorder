package me.taromati.streamerrecorder.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix="record")
public class RecordConfig {

    private String fileDir;

    private Afreeca afreeca;

    @Data
    public static class Afreeca {
        private String username;
        private String password;
    }
}
