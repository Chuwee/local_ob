package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum ApiMgmtInsuranceErrorCode implements FormattableErrorCode {
    INSURANCE_POLICY_NOT_ACCESSIBLE(CONFLICT, "Insurance policy is not accessible for operatorId"),
    INSURANCE_POLICY_IDS_IS_NOT_NULL(BAD_REQUEST, "Insurance policy ids must be null"),
    INSURANCE_POLICES_NOT_ALLOWED_IN_CHANNEL(CONFLICT, "Insurance polices not allowed by channel type"),
    INSURANCE_POLICY_IDS_IS_NULL(BAD_REQUEST, "Insurance police ids cannot be null or empty"),
    CHANNEL_NOT_FOUND(BAD_REQUEST, "Channel not found"),
    INSURANCE_POLICES_NOT_AVAILABLE_FOR_CHANNEL(CONFLICT, "Channel type is not compatible with insurance polices"),
    INSURER_NOT_FOUND(NOT_FOUND, "Insurer not found"),
    INSURANCE_POLICY_NOT_FOUND(NOT_FOUND, "Insurance policies not found"),
    INSURANCE_POLICY_NOT_IN_INSURER(NOT_FOUND, "Policy not found in insurer"),
    TERMS_CONDITIONS_NOT_FOUND(NOT_FOUND, "Terms and conditions not found"),
    INVALID_FILE_CONTENT(BAD_REQUEST, "Invalid file content"),
    INVALID_FILE_NAME(BAD_REQUEST, "Invalid file name"),
    INVALID_FILE_NAME_SPACES(BAD_REQUEST, "The file name cannot contain spaces"),
    INVALID_FILE_NAME_CHARACTERS(BAD_REQUEST, "The file name cannot contain special characters"),
    TERMS_CONDITIONS_FILE_CONTENT_NOT_FOUND(NOT_FOUND, "Terms and conditions file content not found in S3 bucket"),
    BAD_REQUEST_PARAMETER(BAD_REQUEST, "Some of the params are incorrect"),
    CONFLICT_INSURANCE_CHANNEL_PARAMETER(CONFLICT, "Conflict in insurance channel parameter");

    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtInsuranceErrorCode(HttpStatus httpStatus, String message) {
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
