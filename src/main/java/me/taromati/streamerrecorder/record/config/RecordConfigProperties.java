package me.taromati.streamerrecorder.record.config;

import lombok.Getter;
import lombok.Setter;
import me.taromati.streamerrecorder.record.vo.SoopOption;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="record")
public class RecordConfigProperties {
    private String fileDir;
    private Soop soop;

    @Getter
    @Setter
    public static class Soop {
        private String username;
        private String password;
    }

    public SoopOption getSoopOption() {
        if (soop == null
                || soop.getUsername() == null || soop.getUsername().isEmpty()
                || soop.getPassword() == null || soop.getPassword().isEmpty()
        ) {
            return null;
        }
        return SoopOption.builder()
                .username(soop.getUsername())
                .password(soop.getPassword())
                .build();
    }
}
