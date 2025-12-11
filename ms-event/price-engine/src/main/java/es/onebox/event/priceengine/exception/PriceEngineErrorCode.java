package es.onebox.event.priceengine.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PriceEngineErrorCode implements ErrorCode {

    CHANNEL_EVENT_NOT_FOUND("PE0001", HttpStatus.NOT_FOUND, "Channel event not found"),
    EVENT_CHANNEL_NOT_FOUND("PE0002", HttpStatus.NOT_FOUND, "Event channel not found");

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    PriceEngineErrorCode(String errorCode, HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }
}
