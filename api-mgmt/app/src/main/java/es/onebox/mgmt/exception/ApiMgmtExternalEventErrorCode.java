package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum ApiMgmtExternalEventErrorCode implements FormattableErrorCode {

    ENTITY_REQUIRED(BAD_REQUEST, "entity_id query param is required for operator users"),
    EXTERNAL_EVENT_UNSUPPORTED_OPERATION(CONFLICT, "Unsupported event operation: %s"),
    EXTERNAL_EVENT_NOT_FOUND(NOT_FOUND, "External event not found");

    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtExternalEventErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
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
        return this.name();
    }

    @Override
    public String formatMessage(Object... args) {
        return String.format(getMessage(), args);
    }

    public static ErrorCode getByCode(String code) {
        return Stream.of(values())
                .filter(errorCode -> errorCode.getErrorCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
