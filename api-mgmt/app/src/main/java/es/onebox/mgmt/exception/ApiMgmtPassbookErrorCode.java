package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public enum ApiMgmtPassbookErrorCode implements FormattableErrorCode {
    PASSBOOK_DATA_MANDATORY(HttpStatus.BAD_REQUEST, "Passbook data is mandatory"),
    PASSBOOK_ENTITY_ID_MANDATORY(HttpStatus.BAD_REQUEST, "Passbook entity id is mandatory"),
    PASSBOOK_CODE_MANDATORY(HttpStatus.BAD_REQUEST, "Passbook code is mandatory"),
    PASSBOOK_NAME_MANDATORY(HttpStatus.BAD_REQUEST, "Passbook name is mandatory"),
    PASSBOOK_ALREADY_EXISTS(HttpStatus.CONFLICT, "There is a passbook template with given code"),
    PASSBOOK_CODE_UNACCEPTABLE(HttpStatus.BAD_REQUEST, "Passbook code only accepts characters from a to z, numbers and \"_\". It must have a minimum of 3 and a maximum of 25 characters."),
    PASSBOOK_BASE_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "Base template to copy does not exists or does not belong to entity"),
    PASSBOOK_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "Passbook template not found"),
    PASSBOOK_OPERATOR_ID_MANDATORY(HttpStatus.BAD_REQUEST, "Passbook operator id is mandatory"),
    PASSBOOK_LANG_MANDATORY(HttpStatus.BAD_REQUEST, "Passbook lang is mandatory"),
    PASSBOOK_INVALID_LANG(HttpStatus.BAD_REQUEST, "Passbook invalid lang"),
    DELETE_DEFAULT_TEMPLATE(HttpStatus.CONFLICT, "Default passbook cannot be deleted"),
    INVALID_LITERAL_KEY(HttpStatus.BAD_REQUEST, "Invalid literal key"),
    INVALID_DATA_PLACEHOLDER(HttpStatus.BAD_REQUEST, "Invalid data placeholder"),
    INVALID_PASSBOOK_FIELD(HttpStatus.BAD_REQUEST, "Invalid passbook field"),
    INVALID_PASSBOOK_TEMPLATE(HttpStatus.BAD_REQUEST, null, true),
    MEMBER_ORDER_TEMPLATE_DEFAULT(HttpStatus.BAD_REQUEST, "Member order template type can't be default entity passbook template"),
    UNSUPPORTED_TICKET_PASSBOOK_CONTENT(BAD_REQUEST, "STRIP is only updatable for channel type MEMBER"),
    DELETE_LAST_IN_TYPE_FOR_ENTITY(HttpStatus.CONFLICT, null, true),
    MEMBER_ORDER_TEMPLATE_NOT_ALLOWED_IN_ENTITY(HttpStatus.CONFLICT, "The creation of member order templates is not allowed in this entity"),
    DIGITAL_SEASON_TICKET_NOT_ALLOWED_IN_ENTITY(HttpStatus.CONFLICT, "Entity doesn't allow generation of digital season tickets"),
    PASSBOOK_RESOURCE_LOAD_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "Could not load resource to generate passbook");


    private final HttpStatus httpStatus;
    private final String message;
    private final boolean forwardMessage;

    ApiMgmtPassbookErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.forwardMessage = false;
    }

    ApiMgmtPassbookErrorCode(HttpStatus httpStatus, String message, boolean forwardMessage) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.forwardMessage = forwardMessage;
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

    @Override
    public boolean forwardMessage() {
        return forwardMessage;
    }

    public static ErrorCode getByCode(String code) {
        return Stream.of(values())
                .filter(errorCode -> errorCode.getErrorCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
