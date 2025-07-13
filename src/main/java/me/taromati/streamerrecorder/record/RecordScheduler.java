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
                        boolean isLive = switch (streamer.getPlatform()) {
                            case CHZZK -> ChzzkApi.isLive(streamer.getAccountId());
                            case SOOP -> SoopApi.isLive(streamer.getAccountId());
                            case TWITCASTING -> TwitcastingApi.isLive(streamer.getAccountId());
                            default -> true;
                        };
                        log.info("Live Check Result: {}, {}, {}", streamer.getUserName(), streamer.getPlatform(), isLive);
                        if (isLive) {
                            discordBot.sendMessage("방송 시작: " + streamer.getUserName() + " (" + streamer.getPlatform() + ")");
                            ProcessBuilder pb = switch (streamer.getPlatform()) {
                                case SOOP -> new ProcessBuilder(streamer.getCommand(recordConfigProperties.getFileDir(), recordConfigProperties.getSoopOption()));
                                default -> new ProcessBuilder(streamer.getCommand(recordConfigProperties.getFileDir(), null));
                            };
                            pb.redirectErrorStream(true);
                            Process p = pb.start();
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

                            // 프로세스 로그 읽기
                            try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    log.debug("[{}][{}] {}", streamer.getUserName(), streamer.getPlatform(), line);
                                }
                            }

                            // 프로세스가 종료될 때까지 대기
                            int exitCode = p.waitFor();
                            log.info("녹화 프로세스 종료: {}, {}, exitCode={}", streamer.getUserName(), streamer.getPlatform(), exitCode);
                            discordBot.sendMessage("방송 종료: " + streamer.getUserName() + " (" + streamer.getPlatform() + ") - 종료 코드: " + exitCode);
                            streamer.setProcess(null);
                        }
                    } catch (Exception e) {
                        log.error("[RecordScheduler] error : {}", streamer.getUserName(), e);
                        streamer.setProcess(null);
                    }
                });
            }
        }
    }
}
