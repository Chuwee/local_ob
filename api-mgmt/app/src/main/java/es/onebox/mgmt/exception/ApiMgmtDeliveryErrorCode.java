package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

public enum  ApiMgmtDeliveryErrorCode implements FormattableErrorCode {

    CHANNEL_USES_ONEBOX_SERVER(HttpStatus.CONFLICT, "Testing is only available for external STMP servers"),
    SMTP_CONFIGURATION_ERROR(HttpStatus.CONFLICT, "The SMTP server configuration is not correct"),
    EMAIL_IS_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "The email address is not allowed"),
    EMAIL_PREPARATION_ERROR(HttpStatus.CONFLICT, "Error preparing the email"),
    EMAIL_PARSE_ERROR(HttpStatus.CONFLICT, "Error parsing the email"),
    EMAIL_AUTH_ERROR(HttpStatus.CONFLICT, "Error with the email authentication"),
    EMAIL_SEND_ERROR(HttpStatus.CONFLICT, "Error sending the email"),
    EMAIL_CONFIG_ERROR(HttpStatus.CONFLICT, "Error on email config. Check params or email from address"),
    EMAIL_GENERIC_ERROR(HttpStatus.CONFLICT, "Generic email test error");

    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtDeliveryErrorCode(HttpStatus httpStatus, String message) {
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
