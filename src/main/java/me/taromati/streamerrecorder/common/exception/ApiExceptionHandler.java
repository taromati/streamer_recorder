package me.taromati.streamerrecorder.common.exception;

import lombok.extern.slf4j.Slf4j;
import me.taromati.streamerrecorder.common.response.ResponseCode;
import me.taromati.streamerrecorder.common.response.RootResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public RootResponse<Void> exceptionHandler(Exception ex) {
        log.error("Exception", ex);

        return RootResponse.fail(ex);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = RuntimeException.class)
    public RootResponse<Void> runtimeExceptionHandler(RuntimeException ex) {
        log.error("RuntimeException", ex);

        return RootResponse.fail(ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ApiException.class)
    public RootResponse<Void> apiExceptionHandler(ApiException ex) {
        log.error("ApiException", ex);

        return RootResponse.fail(ex);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public RootResponse<Void> apiExceptionHandler(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException", ex);

        return RootResponse.fail(ResponseCode.INVALID_PARAMETER);
    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(value = DBException.class)
//    public RootResponse dbExceptionHandler(DBException ex) {
//        log.error("DBException", ex);
//
//        return new RootResponse<>(ex.getCode(), ex.getMessage());
//    }
}
