package me.taromati.streamerrecorder.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.common.RecordConfig;
import me.taromati.streamerrecorder.manager.StreamerManager;
import me.taromati.streamerrecorder.vo.Streamer;
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

    private final RecordConfig recordConfig;

    private final StreamerManager streamerManager;

    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Scheduled(cron = "*/10 * * * * *")
    public void process() {
        log.info("{}", "##################################################");
        List<Streamer> streamerList = streamerManager.getStreamerList();
        for (Streamer streamer : streamerList) {
            CompletableFuture.runAsync(() -> {
                try {
                    if (streamer.getProcess() == null) {
                        ProcessBuilder pb = new ProcessBuilder(streamer.getCommand(recordConfig.getFileDir()));
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
