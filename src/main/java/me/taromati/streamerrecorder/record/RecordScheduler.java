package me.taromati.streamerrecorder.record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.discord.DiscordBot;
import me.taromati.streamerrecorder.record.config.RecordConfigProperties;
import me.taromati.streamerrecorder.record.vo.Streamer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecordScheduler {
    private final RecordConfigProperties recordConfigProperties;
    private final StreamerManager streamerManager;
    private final DiscordBot discordBot;

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Scheduled(cron = "*/10 * * * * *")
    public void process() {
        // TODO: 녹화 시작 및 종료 시 디스코드로 메시지 전송
        log.info("{}", "##################################################");
//        discordBot.sendMessage("스케줄러 시작");
        List<Streamer> streamerList = streamerManager.getStreamerList();
        for (Streamer streamer : streamerList) {
            CompletableFuture.runAsync(() -> {
                try {
                    if (streamer.getProcess() == null) {
                        ProcessBuilder pb = new ProcessBuilder(streamer.getCommand(recordConfigProperties.getFileDir(), recordConfigProperties.getSoopOption()));
                        Process p = pb.start();
                        streamer.setProcess(p);
                    } else if (!streamer.getProcess().isAlive()) {
                        streamer.getProcess().destroy();
                        streamer.setProcess(null);

//                        sleep(5 * 1000);
//
//                        ProcessBuilder pb = new ProcessBuilder(streamer.getCommand(fileDir));
//                        Process p = pb.start();
//                        streamer.setProcess(p);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    streamer.setProcess(null);
                }
            }, executorService);
        }
        log.info("{}", "##################################################");
    }
}
