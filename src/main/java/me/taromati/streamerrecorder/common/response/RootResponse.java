package me.taromati.streamerrecorder.common.response;

import lombok.Getter;
import me.taromati.streamerrecorder.common.exception.ApiException;

@Getter
public class RootResponse<T> {
    private final String code;
    private final String message;
    private final T data;

    private RootResponse(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = null;
    }

    private RootResponse(T data) {
        this.code = ResponseCode.SUCCESS.getCode();
        this.message = ResponseCode.SUCCESS.getMessage();
        this.data = data;
    }

    private RootResponse(ResponseCode responseCode, T data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }

    private RootResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    private RootResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static RootResponse<Void> ok() {
        return new RootResponse<>(ResponseCode.SUCCESS);
    }

    public static <T> RootResponse<T> ok(T data) {
        return new RootResponse<>(data);
    }

    public static RootResponse<Void> fail(Exception e) {
        String code = ResponseCode.FAIL.getCode();
        String message = ResponseCode.FAIL.getMessage();
        if (e instanceof ApiException) {
            code = ((ApiException) e).getCode();
            message = e.getMessage();
        }

        return new RootResponse<>(code, message);
    }

    public static RootResponse<Void> fail(ResponseCode responseCode) {
        String code = responseCode.getCode();
        String message = responseCode.getMessage();

        return new RootResponse<>(code, message);
    }
}
