package me.taromati.streamerrecorder.record.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@JsonIgnoreProperties(value = {"process"})
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Streamer {
    private final Platform platform;
    private final String accountId;
    private final String userName;

    @Setter
    private Process process;

    @Setter
    private String useYn;

    @Builder
    public Streamer(Platform platform, String accountId, String userName, String useYn) {
        this.platform = platform;
        this.accountId = accountId;
        this.userName = userName;
        this.useYn = useYn;
    }

    public boolean getStatus() {
        return process != null && process.isAlive();
    }
}
