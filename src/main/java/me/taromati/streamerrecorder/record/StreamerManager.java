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
                        .useYn(streamerEntity.getUseYn())
                        .build())
                .forEach(streamerList::add);
        log.info("[StreamerManager] init end");
    }

    public void add(Streamer streamer) {
        StreamerEntity streamerEntity = streamerRepository.findByPlatformAndAccountId(streamer.getPlatform().toString(), streamer.getAccountId())
                .orElse(null);
        if (streamerEntity != null) {
            throw new ApiException(ResponseCode.ALREADY_EXISTS);
        }
        streamerRepository.save(StreamerEntity.builder()
                        .platform(streamer.getPlatform().toString())
                        .accountId(streamer.getAccountId())
                        .userName(streamer.getUserName())
                        .useYn(streamer.getUseYn())
                        .build());
        streamerList.add(streamer);
    }

    public void toggleUseYnByAccountId(Streamer streamer) {
        StreamerEntity streamerEntity = streamerRepository.findByPlatformAndAccountId(streamer.getPlatform().toString(), streamer.getAccountId())
                .orElseThrow(() -> new ApiException(ResponseCode.STREAMER_NOT_FOUND));
        String newValue = "Y".equals(streamerEntity.getUseYn()) ? "N" : "Y";
        streamerEntity.setUseYn(newValue);
        streamerRepository.save(streamerEntity);
        streamerList.stream()
                .filter(s -> s.getPlatform().equals(streamer.getPlatform()) && s.getAccountId().equals(streamer.getAccountId()))
                .findFirst()
                .ifPresent(s -> s.setUseYn(newValue));
    }

    public void onUseYnByUserName(Streamer streamer) {
        StreamerEntity streamerEntity = streamerRepository.findByPlatformAndUserName(streamer.getPlatform().toString(), streamer.getUserName())
                .orElseThrow(() -> new ApiException(ResponseCode.STREAMER_NOT_FOUND));
        streamerEntity.setUseYn("Y");
        streamerRepository.save(streamerEntity);
        streamerList.stream()
                .filter(s -> s.getPlatform().equals(streamer.getPlatform()) && s.getUserName().equals(streamer.getUserName()))
                .findFirst()
                .ifPresent(s -> s.setUseYn("Y"));
    }

    public void offUseYnByUserName(Streamer streamer) {
        StreamerEntity streamerEntity = streamerRepository.findByPlatformAndUserName(streamer.getPlatform().toString(), streamer.getUserName())
                .orElseThrow(() -> new ApiException(ResponseCode.STREAMER_NOT_FOUND));
        streamerEntity.setUseYn("N");
        streamerRepository.save(streamerEntity);
        streamerList.stream()
                .filter(s -> s.getPlatform().equals(streamer.getPlatform()) && s.getUserName().equals(streamer.getUserName()))
                .findFirst()
                .ifPresent(s -> s.setUseYn("N"));
    }

    public boolean isUseYn(Streamer streamer) {
        return streamer.getUseYn() != null && streamer.getUseYn().equals("Y");
    }

    public void deleteByAccountId(Streamer streamer) {
        StreamerEntity streamerEntity = streamerRepository.findByPlatformAndAccountId(streamer.getPlatform().toString(), streamer.getAccountId())
                .orElseThrow(() -> new ApiException(ResponseCode.STREAMER_NOT_FOUND));
        streamerRepository.delete(streamerEntity);
        streamerList.stream()
                .filter(s -> s.getPlatform().equals(streamer.getPlatform()) && s.getAccountId().equals(streamer.getAccountId()))
                .findFirst()
                .ifPresent(streamerList::remove);
    }

    public void deleteByUserName(Streamer streamer) {
        StreamerEntity streamerEntity = streamerRepository.findByPlatformAndUserName(streamer.getPlatform().toString(), streamer.getUserName())
                .orElseThrow(() -> new ApiException(ResponseCode.STREAMER_NOT_FOUND));
        streamerRepository.delete(streamerEntity);
        streamerList.stream()
                .filter(s -> s.getPlatform().equals(streamer.getPlatform()) && s.getUserName().equals(streamer.getUserName()))
                .findFirst()
                .ifPresent(streamerList::remove);
    }
}
