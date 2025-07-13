package me.taromati.streamerrecorder.web.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.web.common.response.RootResponse;
import me.taromati.streamerrecorder.record.StreamerManager;
import me.taromati.streamerrecorder.record.vo.Platform;
import me.taromati.streamerrecorder.record.vo.Streamer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/streamer")
public class StreamerController {

    private final StreamerManager streamerManager;

    @GetMapping("/list")
    public RootResponse<List<Streamer>> getList(
    ) {
        return RootResponse.ok(streamerManager.getStreamerList());
    }

    @PostMapping()
    public RootResponse<Void> add(
            @Valid @RequestBody StreamerRequest request
    ) {
        Streamer streamer = Streamer.builder()
                .platform(Platform.from(request.getPlatform()))
                .accountId(request.getAccountId())
                .userName(request.getUserName())
                .useYn(request.getUseYn() != null ? request.getUseYn() : "Y")
                .build();
        streamerManager.add(streamer);
        return RootResponse.ok();
    }

    @DeleteMapping()
    public RootResponse<Void> delete(
            @Valid @RequestBody StreamerRequest request
    ) {
        Streamer streamer = Streamer.builder()
                .platform(Platform.from(request.getPlatform()))
                .accountId(request.getAccountId())
                .userName(request.getUserName())
                .build();
        streamerManager.deleteByAccountId(streamer);
        return RootResponse.ok();
    }

    @PostMapping("/toggle-record")
    public RootResponse<Void> toggleRecordEnabled(
            @Valid @RequestBody StreamerRequest request
    ) {
        Streamer streamer = Streamer.builder()
                .platform(Platform.from(request.getPlatform()))
                .accountId(request.getAccountId())
                .userName(request.getUserName())
                .build();
        streamerManager.toggleUseYnByAccountId(streamer);
        return RootResponse.ok();
    }
}
