package me.taromati.streamerrecorder.record;

import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.web.common.exception.ApiException;
import me.taromati.streamerrecorder.web.common.response.ResponseCode;
import me.taromati.streamerrecorder.record.db.entity.StreamerEntity;
import me.taromati.streamerrecorder.record.db.repository.StreamerRepository;
import me.taromati.streamerrecorder.record.vo.Platform;
import me.taromati.streamerrecorder.record.vo.Streamer;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Slf4j
@Component
@RequiredArgsConstructor
public class StreamerManager {
    private final StreamerRepository streamerRepository;
    private final List<Streamer> streamerList = Lists.newArrayList();

    @PostConstruct
    private void init() {
        log.info("[StreamerManager] init start");
        streamerRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(streamerEntity -> Streamer.builder()
                        .platform(Platform.from(streamerEntity.getPlatform()))
                        .accountId(streamerEntity.getAccountId())
                        .userName(streamerEntity.getUserName())
                        .build())
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
                        .useYn("Y")
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
