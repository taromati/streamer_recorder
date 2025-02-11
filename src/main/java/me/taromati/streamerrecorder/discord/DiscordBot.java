package me.taromati.streamerrecorder.discord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.discord.config.DiscordConfigProperties;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Slf4j
@RequiredArgsConstructor
@Component
public class DiscordBot {
    private final DiscordConfigProperties discordConfigProperties;
    private final DiscordEventListener discordEventListener;
    private JDA jda;

    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        log.debug("[DiscordBot] onApplicationStartedEvent: {}", event);
        String token = discordConfigProperties.getToken();
        if (token == null || token.isEmpty()) {
            log.warn("[DiscordBot] onApplicationStartedEvent: token is empty");
            return;
        }

        try {
            log.debug("[DiscordBot] onApplicationStartedEvent: boot start discord bot");
            this.jda = JDABuilder.createLight(token, EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                    .addEventListeners(discordEventListener)
                    .build()
                    .awaitReady();
            log.debug("[DiscordBot] onApplicationStartedEvent: boot end discord bot");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        TextChannel channel = getChannel();
        if (channel == null) {
            log.warn("[DiscordBot] sendMessage: channel is null");
            return;
        }

        channel.sendMessage(message).queue();
    }

    private TextChannel getChannel() {
        if (jda == null) {
            return null;
        }

        return jda.getGuildsByName(discordConfigProperties.getServerName(), true).stream()
                .flatMap(guild -> guild.getTextChannelsByName(discordConfigProperties.getChannelName(), true).stream())
                .findFirst()
                .orElse(null);
    }
}
