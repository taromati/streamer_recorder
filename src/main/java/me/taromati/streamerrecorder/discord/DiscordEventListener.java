package me.taromati.streamerrecorder.discord;

import lombok.RequiredArgsConstructor;
import me.taromati.streamerrecorder.record.StreamerManager;
import me.taromati.streamerrecorder.record.vo.Platform;
import me.taromati.streamerrecorder.record.vo.Streamer;
import me.taromati.streamerrecorder.web.common.exception.ApiException;
import me.taromati.streamerrecorder.web.common.response.ResponseCode;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class DiscordEventListener extends ListenerAdapter {
    private final StreamerManager streamerManager;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.startsWith("!recorder") == false && message.startsWith("!녹화") == false) {
            return;
        }

        List<String> command = parseRecorderMessage(message);
        handleRecorderCommand(event, command);

        System.out.printf("[%s] %#s: %s\n",
                event.getChannel(),
                event.getAuthor(),
                event.getMessage().getContentDisplay());
    }

    private List<String> parseRecorderMessage(String message) {
        // 공백을 기준으로 나눠서 !recorder 혹은 !녹화를 제거한(첫번째 요소를 제거한) 나머지를 반환
        List<String> tokens = List.of(message.split(" "));
        return tokens.subList(1, tokens.size());
    }

    private void handleRecorderCommand(MessageReceivedEvent event, List<String> command) {
        // TODO: 기능 완성
        switch (command.getFirst()) {
            case "help", "사용법" -> {
                String helpMessage = """
                        **스트리머 녹화 명령어 사용법**
                        - !녹화 목록: 현재 등록된 스트리머 목록을 보여줍니다.
                        - !녹화 추가 {플랫폼} {accountId} {userName} [on|off]: 스트리머를 추가합니다.
                          - 플랫폼: CHZZK, SOOP, TWITCASTING, TWITCH 중 하나
                          - accountId: 스트리머의 계정 ID
                          - userName: 저장에 사용할 스트리머의 사용자 이름
                          - on|off: 녹화 기능을 켜거나 끌 수 있습니다. (기본값은 on)
                        - !녹화 삭제 {플랫폼} {userName}: 스트리머를 삭제합니다.
                        - !녹화 켜기 {플랫폼} {userName}: 스트리머의 녹화 기능을 켭니다.
                        - !녹화 끄기 {플랫폼} {userName}: 스트리머의 녹화 기능을 끕니다.
                        """;
                event.getChannel().sendMessage(helpMessage).queue();
            }
            case "list", "목록" -> {
                event.getChannel().sendMessage(listStreamers()).queue();
            }
            case "add", "추가" -> event.getChannel().sendMessage(addStreamer(command)).queue();
            case "delete", "삭제" -> event.getChannel().sendMessage(deleteStreamer(command)).queue();
            case "on", "켜기" -> {
                event.getChannel().sendMessage(recordOn(command)).queue();
            }
            case "off", "끄기" -> {
                event.getChannel().sendMessage(recordOff(command)).queue();
            }
            default -> {}
        }
    }

    private String listStreamers() {
        // 녹화 목록 및 상태 확인
        List<Streamer> list =  streamerManager.getStreamerList();

        if (list.isEmpty()) {
            return "녹화 목록이 비어 있습니다.";
        }

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("녹화 목록:\n");
        for (Streamer streamer : list) {
            boolean isUsed = Objects.equals(streamer.getUseYn(), "Y");
            messageBuilder.append(streamer.getUserName())
                    .append(" (")
                    .append(streamer.getPlatform())
                    .append(") - 녹화기능: ")
                    .append(isUsed ? "ON" : "OFF");
            if (isUsed) {
                messageBuilder.append(" - 녹화상태: ")
                        .append(streamer.getStatus() ? "녹화 중" : "대기 중");
            }
            messageBuilder.append("\n");
        }

        return messageBuilder.toString();
    }
    private String addStreamer(List<String> command) {
        // 예시: !녹화 추가 플랫폼 accountId userName [on|off]
        if (command.size() < 4) {
            return "[오류] 명령어 형식이 잘못되었습니다: !녹화 추가 {플랫폼} {accountId} {userName} [on|off]\n허용 플랫폼: CHZZK, SOOP, TWITCASTING, TWITCH";
        }

        String platformString = command.get(1);
        Platform platform = null;
        try {
            platform = Platform.from(platformString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return String.format("[오류] 플랫폼이 잘못되었습니다: %s\n허용 플랫폼: CHZZK, SOOP, TWITCASTING, TWITCH", platformString);
        }
        String accountId = command.get(2);
        String userName = command.get(3);
        String useYn = "Y";
        if (command.size() > 4) {
            String onOff = command.get(4).toUpperCase();
            if (onOff.equals("ON") || onOff.equals("OFF")) {
                useYn = onOff.equals("ON") ? "Y" : "N";
            } else {
                return "[오류] 녹화기능 값은 on 또는 off만 허용됩니다.";
            }
        }

        try {
            Streamer streamer = Streamer.builder()
                    .platform(platform)
                    .accountId(accountId)
                    .userName(userName)
                    .useYn(useYn)
                    .build();
            streamerManager.add(streamer);
            return String.format("[성공] 스트리머 추가: %s (%s) 녹화기능: %s", userName, platform, useYn);
        } catch (ApiException e) {
            ResponseCode responseCode = ResponseCode.fromCode(e.getCode());
            return switch (responseCode) {
                case ResponseCode.ALREADY_EXISTS -> "[오류] 이미 존재하는 스트리머입니다";
                case null, default -> String.format("[오류] 스트리머 추가 실패: %s", e.getMessage());
            };
        } catch (Exception e) {
            return String.format("[오류] 스트리머 추가 실패: %s", e.getMessage());
        }
    }
    private String deleteStreamer(List<String> command) {
        // 예시: !녹화 삭제 플랫폼 userName
        if (command.size() < 3) {
            return "[오류] 명령어 형식이 잘못되었습니다: !녹화 삭제 {플랫폼} {userName}\n허용 플랫폼: CHZZK, SOOP, TWITCASTING, TWITCH";
        }

        String platformString = command.get(1);
        Platform platform = null;
        try {
            platform = Platform.from(platformString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return String.format("[오류] 플랫폼이 잘못되었습니다: %s\n허용 플랫폼: CHZZK, SOOP, TWITCASTING, TWITCH", platformString);
        }
        String userName = command.get(2);

        try {
            Streamer streamer = Streamer.builder()
                    .platform(platform)
                    .userName(userName)
                    .build();
            streamerManager.deleteByUserName(streamer);
            return String.format("[성공] 스트리머 삭제: %s (%s)", userName, platform);
        } catch (ApiException e) {
            ResponseCode responseCode = ResponseCode.fromCode(e.getCode());
            return switch (responseCode) {
                case ResponseCode.STREAMER_NOT_FOUND -> "[오류] 해당 스트리머를 찾을 수 없습니다.";
                case null, default -> String.format("[오류] 스트리머 삭제 실패: %s", e.getMessage());
            };
        } catch (Exception e) {
            return String.format("[오류] 스트리머 삭제 실패: %s", e.getMessage());
        }
    }

    private String recordOn(List<String> command) {
        // 예시: !녹화 켜기 플랫폼 userName
        if (command.size() < 2) {
            return "[오류] 명령어 형식이 잘못되었습니다: !녹화 on {플랫폼} {userName}\n허용 플랫폼: CHZZK, SOOP, TWITCASTING, TWITCH";
        }
        String platformString = command.get(1);
        Platform platform = null;
        try {
            platform = Platform.from(platformString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return String.format("[오류] 플랫폼이 잘못되었습니다: %s\n허용 플랫폼: CHZZK, SOOP, TWITCASTING, TWITCH", platformString);
        }
        String userName = command.get(2);

        try {
            Streamer streamer = Streamer.builder()
                    .platform(platform)
                    .userName(userName)
                    .build();
            streamerManager.onUseYnByUserName(streamer);
            return String.format("[성공] %s (%s) 녹화기능을 켰습니다.", userName, platform);
        } catch (ApiException e) {
            ResponseCode responseCode = ResponseCode.fromCode(e.getCode());
            return switch (responseCode) {
                case ResponseCode.STREAMER_NOT_FOUND -> "[오류] 해당 스트리머를 찾을 수 없습니다.";
                case null, default -> String.format("[오류] 녹화기능 켜기 실패: %s", e.getMessage());
            };
        } catch (Exception e) {
            return String.format("[오류] 녹화기능 켜기 실패: %s", e.getMessage());
        }
    }

    private String recordOff(List<String> command) {
        // 예시: !녹화 끄기 플랫폼 userName
        if (command.size() < 2) {
            return "[오류] 명령어 형식이 잘못되었습니다: !녹화 off {플랫폼} {userName}\n허용 플랫폼: CHZZK, SOOP, TWITCASTING, TWITCH";
        }
        String platformString = command.get(1);
        Platform platform = null;
        try {
            platform = Platform.from(platformString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return String.format("[오류] 플랫폼이 잘못되었습니다: %s\n허용 플랫폼: CHZZK, SOOP, TWITCASTING, TWITCH", platformString);
        }
        String userName = command.get(2);

        try {
            Streamer streamer = Streamer.builder()
                    .platform(platform)
                    .userName(userName)
                    .build();
            streamerManager.offUseYnByUserName(streamer);
            return String.format("[성공] %s (%s) 녹화기능을 껐습니다.", userName, platform);
        } catch (ApiException e) {
            ResponseCode responseCode = ResponseCode.fromCode(e.getCode());
            return switch (responseCode) {
                case ResponseCode.STREAMER_NOT_FOUND -> "[오류] 해당 스트리머를 찾을 수 없습니다.";
                case null, default -> String.format("[오류] 녹화기능 끄기 실패: %s", e.getMessage());
            };
        } catch (Exception e) {
            return String.format("[오류] 녹화기능 끄기 실패: %s", e.getMessage());
        }
    }

}
