package me.taromati.streamerrecorder.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.common.response.RootResponse;
import me.taromati.streamerrecorder.manager.StreamerManager;
import me.taromati.streamerrecorder.vo.Platform;
import me.taromati.streamerrecorder.vo.Streamer;
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
        streamerManager.delete(streamer);
        return RootResponse.ok();
    }
}
