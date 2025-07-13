package me.taromati.streamerrecorder.web.common.response;

import lombok.Getter;

@Getter
public enum ResponseCode {
    SUCCESS("OK", "성공."),/**/
    FAIL("FAIL", "예기치 못한 오류가 발생했습니다. 나중에 다시 시도해 주세요."),/**/
    // 400
    INVALID_PARAMETER("INVALID_PARAMETER", "필수 파라미터 누락 혹은 잘못된 파라미터가 존재 합니다."),/**/
    ALREADY_EXISTS("ALREADY_EXISTS", "이미 등록된 스트리머입니다."),/**/
    STREAMER_NOT_FOUND("STREAMER_NOT_FOUND", "해당 스트리머를 찾을 수 없습니다."),/**/

    // 500
    INVALID_DATABASE("INVALID_DATABASE", "예기치 못한 오류가 발생했습니다. 나중에 다시 시도해 주세요."),/**/
    ;

    private final String code;

    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResponseCode fromCode(String code) {
        for (ResponseCode responseCode : values()) {
            if (responseCode.getCode().equals(code)) {
                return responseCode;
            }
        }
        return null;
    }
}
