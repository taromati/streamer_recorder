package me.taromati.streamerrecorder.manager;

import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.common.RecordConfig;
import me.taromati.streamerrecorder.common.exception.ApiException;
import me.taromati.streamerrecorder.common.response.ResponseCode;
import me.taromati.streamerrecorder.db.entity.StreamerEntity;
import me.taromati.streamerrecorder.db.repository.StreamerRepository;
import me.taromati.streamerrecorder.vo.AfreecaOption;
import me.taromati.streamerrecorder.vo.Platform;
import me.taromati.streamerrecorder.vo.Streamer;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Getter
@Slf4j
@Component
@RequiredArgsConstructor
public class StreamerManager {

    private final RecordConfig recordConfig;

    private final List<Streamer> streamerList = Lists.newArrayList();

    private final StreamerRepository streamerRepository;

    @PostConstruct
    private void init() {
        log.info("[StreamerManager] init start");
        AfreecaOption afreecaOption = Optional.ofNullable(recordConfig.getAfreeca())
                .filter(afreeca -> (afreeca.getUsername() != null && !afreeca.getUsername().isEmpty()) && (afreeca.getPassword() != null && !afreeca.getPassword().isEmpty()))
                .map(afreeca -> AfreecaOption.builder()
                        .username(afreeca.getUsername())
                        .password(afreeca.getPassword())
                        .build())
                .orElse(null);

        streamerRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream().map(streamerEntity ->
                Streamer.builder()
                        .platform(Platform.from(streamerEntity.getPlatform()))
                        .accountId(streamerEntity.getAccountId())
                        .userName(streamerEntity.getUserName())
                        .afreecaOption(afreecaOption)
                        .build()
                )
                .forEach(streamerList::add);
        log.info("[StreamerManager] init end");
    }

    public void add(Streamer streamer) {
        StreamerEntity streamerEntity = streamerRepository.findByPlatformAndAccountId(streamer.getPlatform().toString(), streamer.getAccountId())
                .orElse(null);
        if (streamerEntity != null) {
            throw new ApiException(ResponseCode.INVALID_PARAMETER);
        }
        streamerRepository.save(StreamerEntity.builder()
                        .platform(streamer.getPlatform().toString())
                        .accountId(streamer.getAccountId())
                        .userName(streamer.getUserName())
                        .build());
        streamerList.add(streamer);
    }

    public void delete(Streamer streamer) {
        StreamerEntity streamerEntity = streamerRepository.findByPlatformAndAccountId(streamer.getPlatform().toString(), streamer.getAccountId())
                .orElseThrow(() -> new ApiException(ResponseCode.INVALID_PARAMETER));
        streamerRepository.delete(streamerEntity);
        streamerList.stream()
                .filter(s -> s.getPlatform().equals(streamer.getPlatform()) && s.getAccountId().equals(streamer.getAccountId()))
                .findFirst()
                .ifPresent(streamerList::remove);
    }

}
