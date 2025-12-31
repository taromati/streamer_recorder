package me.taromati.streamerrecorder.record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.api.soop.SoopApi;
import me.taromati.streamerrecorder.api.twitcasting.TwitcastingApi;
import me.taromati.streamerrecorder.discord.DiscordBot;
import me.taromati.streamerrecorder.record.config.RecordConfigProperties;
import me.taromati.streamerrecorder.record.vo.Streamer;
import me.taromati.streamerrecorder.api.chzzk.ChzzkApi;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordScheduler {
    private final RecordConfigProperties recordConfigProperties;
    private final StreamerManager streamerManager;
    private final DiscordBot discordBot;

    @Scheduled(cron = "*/10 * * * * *")
    public void process() {
        log.info("[RecordScheduler] {}", "##################################################");
        List<Streamer> streamerList = streamerManager.getStreamerList();
        for (Streamer streamer : streamerList) {
            // useYn이 'Y'인 스트리머만 녹화 시도
            if ((!streamerManager.isUseYn(streamer))) {
                continue;
            }
            // 녹화중이 아닌 스트리머만 처리
            if (streamer.getProcess() == null || streamer.getProcess().isAlive() == false) {
                Thread.startVirtualThread(() -> {
                    try {
                        log.info("Live Check: {}, {}", streamer.getUserName(), streamer.getPlatform());
                        String liveTitle = switch (streamer.getPlatform()) {
                            case CHZZK -> ChzzkApi.isLiveNGetLiveTitle(streamer.getAccountId());
                            case SOOP -> SoopApi.isLiveNGetLiveTitle(streamer.getAccountId());
                            case TWITCASTING -> TwitcastingApi.isLiveNGetLiveTitle(streamer.getAccountId());
                            default -> null;
                        };
                        log.info("Live Check Result: {}, {}, {}", streamer.getUserName(), streamer.getPlatform(), liveTitle);
                        if (liveTitle == null || liveTitle.isEmpty()) {
                            return; // 방송이 시작되지 않았거나, 방송 제목을 가져오지 못한 경우
                        }

                        discordBot.sendMessage("방송 시작: " + streamer.getUserName() + " (" + streamer.getPlatform() + ") - " + liveTitle);
                        String tsFilePath = RecordUtil.makeFilePath(streamer, recordConfigProperties.getFileDir(), liveTitle, "ts");

                        ProcessBuilder streamlinkPb = switch (streamer.getPlatform()) {
                            case SOOP -> new ProcessBuilder(RecordUtil.getRecordCommand(streamer, tsFilePath, recordConfigProperties.getSoop(), null));
                            case CHZZK -> new ProcessBuilder(RecordUtil.getRecordCommand(streamer, tsFilePath, null, recordConfigProperties.getChzzk()));
                            default -> new ProcessBuilder(RecordUtil.getRecordCommand(streamer, tsFilePath, null, null));
                        };
//                        streamlinkPb.redirectErrorStream(true);
                        Process p = streamlinkPb.start();
                        streamer.setProcess(p);

                        // 종료 훅 등록
                        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                            if (p != null && p.isAlive()) {
                                log.info("애플리케이션 종료: 외부 프로세스 정상 종료 시도 - {}, {}", streamer.getUserName(), streamer.getPlatform());
                                p.destroy();
                                try {
                                    p.waitFor();
                                } catch (InterruptedException e) {
                                    p.destroyForcibly();
                                }
                                log.info("외부 프로세스 종료 완료 - {}, {}", streamer.getUserName(), streamer.getPlatform());
                            }
                        }));

                        // Streamlink 프로세스 로그 읽기
//                        try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
//                            String line;
//                            while ((line = reader.readLine()) != null) {
//                                log.debug("[Streamlink][{}][{}] {}", streamer.getUserName(), streamer.getPlatform(), line);
//                            }
//                        }

                        // 프로세스가 종료될 때까지 대기
                        int exitCode = p.waitFor();
                        log.info("녹화 프로세스 종료: {}, {}, exitCode={}", streamer.getUserName(), streamer.getPlatform(), exitCode);
                        discordBot.sendMessage("방송 종료: " + streamer.getUserName() + " (" + streamer.getPlatform() + ") - 종료 코드: " + exitCode);
                        streamer.setProcess(null);

                        if (recordConfigProperties.getUseTransformMp4()) {
                            // ffmpeg 변환을 별도의 쓰레드에서 실행
                            Thread.startVirtualThread(() -> {
                                try {
                                    log.info("ffmpeg 변환 시작: {}, {}", streamer.getUserName(), streamer.getPlatform());
                                    ProcessBuilder ffmpegPb = new ProcessBuilder(RecordUtil.getFfmpegTransformCommand(tsFilePath));
                                    ffmpegPb.redirectErrorStream(true);
                                    Process ffmpegProcess = ffmpegPb.start();

                                    // ffmpeg 프로세스 로그 읽기
                                    try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(ffmpegProcess.getInputStream()))) {
                                        String line;
                                        while ((line = reader.readLine()) != null) {
                                            System.out.println("[ffmpeg] " + line);
                                        }
                                    }
                                    int ffmpegExitCode = ffmpegProcess.waitFor();
                                    log.info("ffmpeg 변환 완료: {}, {}, exitCode={}", streamer.getUserName(), streamer.getPlatform(), ffmpegExitCode);
                                    discordBot.sendMessage("mp4 변환 완료: " + streamer.getUserName() + " (" + streamer.getPlatform() + ") - ffmpeg 종료 코드: " + ffmpegExitCode);
                                    if (recordConfigProperties.getDeleteTs()) {
                                        // 변환 후 ts 파일 삭제
                                        try {
                                            File tsFile = new File(tsFilePath);
                                            if (tsFile.exists() && tsFile.delete()) {
                                                log.info("TS 파일 삭제 성공: {}", tsFilePath);
                                            } else {
                                                discordBot.sendMessage("TS 파일 삭제 실패 또는 파일이 존재하지 않음: " + tsFilePath);
                                                log.warn("TS 파일 삭제 실패 또는 파일이 존재하지 않음: {}", tsFilePath);
                                            }
                                        } catch (Exception e) {
                                            log.error("TS 파일 삭제 중 오류 발생: {}", tsFilePath, e);
                                            discordBot.sendMessage("TS 파일 삭제 중 오류 발생: " + tsFilePath + " - " + e.getMessage());
                                        }
                                    }
                                } catch (Exception e) {
                                    log.error("[RecordScheduler][ffmpeg] error : {}", streamer.getUserName(), e);
                                    discordBot.sendMessage("mp4 변환 오류: " + streamer.getUserName() + " (" + streamer.getPlatform() + ") - \n" + e.getMessage());
                                }
                            });
                        }
                    } catch (Exception e) {
                        log.error("[RecordScheduler] error : {}", streamer.getUserName(), e);
                        streamer.setProcess(null);
                        discordBot.sendMessage("녹화 오류: " + streamer.getUserName() + " (" + streamer.getPlatform() + ") - \n" + e.getMessage());
                    }
                });
            }
        }
    }
}
