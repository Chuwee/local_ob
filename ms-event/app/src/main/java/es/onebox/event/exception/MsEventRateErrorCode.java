package es.onebox.event.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum MsEventRateErrorCode implements ErrorCode {
    INVALID_RATE("ME0014", HttpStatus.PRECONDITION_FAILED, "Invalid rates configuration"),
    RATE_HAS_SESSIONS("ME0022", HttpStatus.FORBIDDEN, "Rate has sessions associated"),
    RATE_HAS_PROMOTIONS("ME0023", HttpStatus.FORBIDDEN, "Rate has promotions associated"),
    RATE_HAS_SALES("ME0024", HttpStatus.FORBIDDEN, "Rate has sales"),
    ID_RATES_NOT_COHERENT("ME0044", HttpStatus.CONFLICT, "Id rates are not coherent"),
    RATE_NOT_FOUND("ME0125", HttpStatus.NOT_FOUND, "Rate not found"),
    NOT_MODIFIABLE_DEFAULT_RATE("ME0130", HttpStatus.CONFLICT, "Rate not modifiable"),
    INVALID_RATE_RESTRICTIONS("INVALID_RATE_RESTRICTIONS", HttpStatus.PRECONDITION_FAILED, "Invalid rate restrictions configuration"),
    RATE_EXTERNAL_TYPE_REQUIRED("RATE_EXTERNAL_TYPE_REQUIRED", HttpStatus.BAD_REQUEST, "Rate external type is required"),
    RATE_EDITING_BLOCKED("RATE_EDITING_BLOCKED", HttpStatus.CONFLICT, "Rate editing is blocked for events when both event and session are ready");

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    MsEventRateErrorCode(String errorCode, HttpStatus httpStatus, String message) {
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

    private static class Constants {
        public static final String INVALID_DATES_FOR_SESSION = "Invalid dates for session";
    }
}
