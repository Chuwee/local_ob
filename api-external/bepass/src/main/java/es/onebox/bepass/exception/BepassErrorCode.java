package es.onebox.bepass.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BepassErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request"),
    INVALID_FIELD(HttpStatus.BAD_REQUEST, "Invalid request field"),
    MISSING_ENTITY_CONFIGURATION(HttpStatus.CONFLICT, "Missing required entity config"),
    INVALID_COMPANY_ID(HttpStatus.CONFLICT, "Invalid company id"),
    BEPASS_GENERIC_ERROR(HttpStatus.BAD_GATEWAY, "Bepass generic error"),
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "Invalid phone number" );
    private HttpStatus httpStatus;
    private String message;

    BepassErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getErrorCode() {
        return name();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
