package me.taromati.streamerrecorder.discord;

import lombok.RequiredArgsConstructor;
import me.taromati.streamerrecorder.record.StreamerManager;
import me.taromati.streamerrecorder.record.vo.Streamer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DiscordEventListener extends ListenerAdapter {
    private final StreamerManager streamerManager;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (!message.startsWith("!")) {
            return;
        }

        switch (message) {
            case "!recorder", "!레코더" -> {
                List<String> command = parseRecorderMessage(message);
                handleRecorderCommand(event, command);
            }
            default -> {}
        };

        System.out.printf("[%s] %#s: %s\n",
                event.getChannel(),
                event.getAuthor(),
                event.getMessage().getContentDisplay());
    }

    private List<String> parseRecorderMessage(String message) {
        // 공백을 기준으로 나눠서 !recorder 혹은 !레코더를 제거한(첫번째 요소를 제거한) 나머지를 반환
        return List.of(message.split(" ")).subList(1, message.length());
    }

    private void handleRecorderCommand(MessageReceivedEvent event, List<String> command) {
        // TODO: 기능 완성
        switch (command.get(0)) {
            case "list", "목록" -> {
                var list = listRecorders();
                event.getChannel().sendMessage(list.toString()).queue();
            }
            case "add", "추가" -> addRecorder(command);
            case "delete", "삭제" -> deleteRecorder(command);
            default -> {}
        }
    }
    private List<Streamer> listRecorders() {
        // 녹화 목록 및 상태 확인
        return streamerManager.getStreamerList();
    }
    private void addRecorder(List<String> command) {
        // 녹화 추가
    }
    private void deleteRecorder(List<String> command) {
        // 녹화 삭제
    }
}
