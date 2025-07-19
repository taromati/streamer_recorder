package me.taromati.streamerrecorder.record;

import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.record.config.RecordConfigProperties;
import me.taromati.streamerrecorder.record.vo.Platform;
import me.taromati.streamerrecorder.record.vo.Streamer;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
public class RecordUtil {
    public static String recordUrl(Streamer streamer) {
        return switch (streamer.getPlatform()) {
            case TWITCH -> STR."twitch.tv/\{streamer.getAccountId()}";
            case CHZZK -> STR."https://chzzk.naver.com/live/\{streamer.getAccountId()}";
            case TWITCASTING -> STR."https://twitcasting.tv/\{streamer.getAccountId()}";
            case SOOP -> STR."https://play.sooplive.co.kr/\{streamer.getAccountId()}";
            default -> throw new IllegalStateException(STR."Unexpected value: \{streamer.getAccountId()}");
        };
    }

    public static String makeFilePath(Streamer streamer, String fileDir, String extension) {
        if (extension == null) {
            extension = "ts"; // 기본 확장자 설정
        }

        String dateTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.SSS"));
        return Paths.get(fileDir, streamer.getUserName(), STR."\{dateTimeString}-\{streamer.getPlatform().name()}-\{streamer.getUserName()}.\{extension}").toString();
    }

    public static String transFileExtension(String filePath, String extension) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        if (extension == null || extension.isEmpty()) {
            return filePath; // 확장자가 없으면 원래 파일 경로 반환
        }
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return filePath + "." + extension; // 확장자가 없는 경우
        }
        return filePath.substring(0, lastDotIndex + 1) + extension; // 기존 확장자 제거 후 새 확장자 추가
    }

    public static String[] getRecordCommand(Streamer streamer, String filePath, RecordConfigProperties.Soop soopOption, RecordConfigProperties.Chzzk chzzkOption) {
        Platform platform = streamer.getPlatform();
        String[] command = switch (streamer.getPlatform()) {
            case TWITCH, TWITCASTING -> new String[]{"streamlink", recordUrl(streamer), "best", "-o", filePath};
            case SOOP -> {
                if (soopOption == null) {
                    yield new String[]{"streamlink", "--ffmpeg-copyts", recordUrl(streamer), "best", "-o", filePath};
                } else {
                    yield new String[]{"streamlink", "--ffmpeg-copyts", recordUrl(streamer), "best", "-o", filePath, "--soop-username", soopOption.getUsername(), "--soop-password", soopOption.getPassword(), "--soop-purge-credentials"};
                }
            }
//            case CHZZK -> new String[]{"streamlink", "--ffmpeg-copyts", recordUrl(streamer), "best", "-o", filePath};
            case CHZZK -> {
                if (chzzkOption == null) {
                    yield new String[]{"streamlink", "--ffmpeg-copyts", "--plugin-dirs", "./plugins", recordUrl(streamer), "best", "-o", filePath};
                } else {
                    yield new String[]{"streamlink", "--ffmpeg-copyts", "--plugin-dirs", "./plugins", "--chzzk-cookies", STR."NID_AUT=\{chzzkOption.getNID_AUT()}; NID_SES=\{chzzkOption.getNID_SES()};", recordUrl(streamer), "best", "-o", filePath};
                }
            }
            default -> throw new IllegalStateException(STR."Unexpected value: \{platform}");
        };

        log.trace("Command record: {}", Arrays.toString(command));
        return command;
    }

    public static String[] getFfmpegTransformCommand(String tsFilePath) {
        String[] command = new String[]{"ffmpeg", "-i", tsFilePath, "-c", "copy", transFileExtension(tsFilePath, "mp4")};

        log.trace("Command ffmpeg: {}", Arrays.toString(command));
        return command;
    }
}
