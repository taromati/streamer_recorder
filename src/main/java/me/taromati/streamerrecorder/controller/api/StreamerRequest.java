package me.taromati.streamerrecorder.controller.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StreamerRequest {
    @NotBlank
    private String platform;
    @NotBlank
    private String accountId;
    @NotBlank
    private String userName;
}
