package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

public enum ApiMgmtExportsErrorCode implements FormattableErrorCode {

    EXPORT_LIMIT_REACHED(HttpStatus.BAD_REQUEST, "Cannot request more than two exportation processes simultaneously."),
    EXPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "Export ID not found"),
    EXPORT_WITH_TOO_MANY_RECORDS(HttpStatus.CONFLICT, "Too much elements to export. Limit your request using the available filters."),
    CURRENCY_NOT_FOUND(HttpStatus.NOT_FOUND, "Currency not found"),
    CURRENCIES_NOT_EQUALS(HttpStatus.BAD_REQUEST, "request currency is different than provider-client currency"),
    S3_FILE_NOT_UPLOADED(HttpStatus.INTERNAL_SERVER_ERROR, "S3 file status: %s"),
    AMQP_PUSH_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send message to the queue"),
    EXPORT_STATUS_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "Export status not found for provided ID")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtExportsErrorCode(HttpStatus httpStatus, String message) {
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
