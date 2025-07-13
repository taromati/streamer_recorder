package me.taromati.streamerrecorder.logging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "logging.level")
public class LoggingLevelProperties {
    private Map<String, String> levels;
}

