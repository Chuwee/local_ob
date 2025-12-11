package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum ApiMgmtAccessControlErrorCode implements FormattableErrorCode {
    ACCESS_CONTROL_SYSTEM_MANDATORY(HttpStatus.BAD_REQUEST, "Access control system is mandatory"),
    ACCESS_CONTROL_SYSTEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Access control system not found"),
    SYSTEM_ENTITY_EXISTS(CONFLICT, "system entity association already exists"),
    VENUE_SYSTEM_NOT_ASSOCIATED(CONFLICT, "Venue is not associated with this system"),
    VENUE_HAS_NO_SYSTEM(NOT_FOUND, "Venue has no associated system"),
    SYSTEM_VENUE_EXISTS(CONFLICT, "system venue association already exists"),
    SKIDATA_CONFIG_MANDATORY(HttpStatus.BAD_REQUEST, "skidata config is mandatory"),
    SKIDATA_CONFIG_HOST_MANDATORY(HttpStatus.BAD_REQUEST, "skidata config host is mandatory"),
    SKIDATA_CONFIG_PORT_MANDATORY(HttpStatus.BAD_REQUEST, "skidata config port is mandatory"),
    SKIDATA_CONFIG_ISSUER_MANDATORY(HttpStatus.BAD_REQUEST, "skidata config issuer is mandatory"),
    SKIDATA_CONFIG_RECEIVER_MANDATORY(HttpStatus.BAD_REQUEST, "skidata config receiver is mandatory"),
    SKIDATA_CONFIG_ALREADY_EXISTS(CONFLICT, "Skidata configuration already exists for this venue"),
    PENDING_EXTERNAL_BARCODE_IMPORT(HttpStatus.PRECONDITION_FAILED, "There is a barcode import not finished for this session");

    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtAccessControlErrorCode(HttpStatus httpStatus, String message) {
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
