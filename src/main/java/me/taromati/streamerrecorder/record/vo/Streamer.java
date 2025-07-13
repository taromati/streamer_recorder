package me.taromati.streamerrecorder.record.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
@Getter
@JsonIgnoreProperties(value = {"process"})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Streamer {
    private final Platform platform;
    private final String accountId;
    private final String userName;

    @Setter
    private Process process;

    @Builder
    public Streamer(Platform platform, String accountId, String userName) {
        this.platform = platform;
        this.accountId = accountId;
        this.userName = userName;
    }

    public boolean getStatus() {
        return process != null && process.isAlive();
    }

    public String makeFilePath(String fileDir) {
        String dateTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS"));
//        return STR."\{fileDir}\{}\{dateTimeString}-\{userName}.ts";
        return Paths.get(fileDir, userName, STR."\{dateTimeString}-\{this.platform.name()}-\{userName}.ts").toString();
    }

    public String recordUrl() {
        return switch (platform) {
            case TWITCH -> STR."twitch.tv/\{accountId}";
            case CHZZK -> STR."https://chzzk.naver.com/live/\{accountId}";
            case TWITCASTING -> STR."https://twitcasting.tv/\{accountId}";
            case YOUTUBE -> STR."https://www.youtube.com/@\{accountId}";
            case SOOP -> STR."https://play.sooplive.co.kr/\{accountId}";
            default -> throw new IllegalStateException(STR."Unexpected value: \{platform}");
        };
    }

    public String[] getCommand(String fileDir, SoopOption soopOption) {
        String[] command = switch (platform) {
            case TWITCH, TWITCASTING, YOUTUBE -> new String[]{"streamlink", recordUrl(), "best", "-o", makeFilePath(fileDir)};
            case SOOP -> {
                if (soopOption == null) {
                    yield new String[]{"streamlink", "--ffmpeg-copyts", recordUrl(), "best", "-o", makeFilePath(fileDir)};
                } else {
                    yield new String[]{"streamlink", "--ffmpeg-copyts", recordUrl(), "best", "-o", makeFilePath(fileDir), "--soop-username", soopOption.getUsername(), "--soop-password", soopOption.getPassword(), "--soop-purge-credentials"};
                }
            }
//            case CHZZK -> new String[]{"streamlink", "--plugin-dirs", "./plugins", "--ffmpeg-copyts", recordUrl(), "best", "-o", makeFilePath(fileDir)};
            case CHZZK -> new String[]{"streamlink", "--ffmpeg-copyts", recordUrl(), "best", "-o", makeFilePath(fileDir)};
            default -> throw new IllegalStateException(STR."Unexpected value: \{platform}");
        };

        log.trace("Command: {}", Arrays.toString(command));
        return command;
    }
}
