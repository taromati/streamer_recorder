package me.taromati.streamerrecorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import me.taromati.streamerrecorder.logging.LoggingLevelProperties;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

@EnableScheduling
@SpringBootApplication
public class StreamerRecorderApplication implements ApplicationRunner {

    private final LoggingLevelProperties loggingLevelProperties;

    public StreamerRecorderApplication(LoggingLevelProperties loggingLevelProperties) {
        this.loggingLevelProperties = loggingLevelProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (loggingLevelProperties.getLevels() != null) {
            loggingLevelProperties.getLevels().forEach((pkg, lvl) -> {
                loggerContext.getLogger(pkg).setLevel(Level.toLevel(lvl));
            });
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(StreamerRecorderApplication.class, args);
    }

}
