package me.taromati.streamerrecorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StreamerRecorderApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamerRecorderApplication.class, args);
    }

}
