package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

public enum ApiMgmtCustomersErrorCode implements FormattableErrorCode {

    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer or user can not be found"),
    CUSTOMER_PARAMETERS_ARE_WRONG(BAD_REQUEST, "One or more parameters are wrong"),
    CUSTOMER_ALREADY_EXISTS(CONFLICT, "Customer already exists"),
    CUSTOMER_BAD_PARAMETER_FORMAT(BAD_REQUEST, "One or more parameters don't have the right format"),
    CUSTOMER_ENTITY_REQUIRED(BAD_REQUEST, "entity_id query param is required for operator users"),
    CUSTOMER_CUSTOMERS_NOT_ALLOW_FOR_ENTITY(CONFLICT, "Customers are not allowed for this entity"),
    CUSTOMER_DUPLICATED_MEMBER_ID(CONFLICT, "Member id is duplicated for this entity"),
    CUSTOMER_LANGUAGE_NOT_VALID(BAD_REQUEST, "Customer language not valid"),
    CUSTOMER_ENTITY_FORBIDDEN(HttpStatus.FORBIDDEN, "Customers are not allowed for user entity"),
    CUSTOMER_NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer note can not be found"),
    CUSTOMER_DELETE_NOT_ALLOWED_FOR_ENTITY(HttpStatus.CONFLICT, "Customers delete is not allowed for this entity"),
    CUSTOMER_DELETE_NOT_ALLOWED_ACTIVE_SEASON_TICKET_MEMBER_REQUIRED(HttpStatus.CONFLICT, "Customer delete is not allowed, the customer has an active season ticket with member required"),
    CUSTOMER_TARGET_ALREADY_MANAGED(HttpStatus.CONFLICT, "Target customer is managed by another customer"),
    CUSTOMER_DUPLICATED_MANAGEMENT(HttpStatus.CONFLICT, "Target customer is already managed"),
    CUSTOMER_INVALID_TYPE(HttpStatus.CONFLICT, "BASIC customers can not manage customers"),
    CUSTOMER_TARGET_INVALID_TYPE(HttpStatus.CONFLICT, "BASIC customers can not be managed"),
    CUSTOMER_TARGET_IS_MANAGER(HttpStatus.CONFLICT, "Customer target is already managing other customers"),
    CUSTOMER_TARGET_NOT_MANAGED(HttpStatus.CONFLICT, "Customer is not manager from target customer"),
    CUSTOMER_IS_MANAGED(HttpStatus.CONFLICT, "Customer is already managed"),
    CUSTOMER_ALREADY_LOCKED(HttpStatus.CONFLICT, "Customer can't be locked because it is not active"),
    CUSTOMER_ALREADY_ACTIVE(HttpStatus.CONFLICT, "Customer can't be unlocked because it is not locked"),
    CUSTOMER_IMPORT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Customer import can't be performed"),
    PENDING_CUSTOMER_IMPORT(HttpStatus.PRECONDITION_FAILED, "There is a customer import not finished for this entity"),
    CLIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Client not found"),
    CONDITIONS_NOT_FOUND(HttpStatus.NOT_FOUND, "Conditions not found"),
    CONDITIONS_NOT_ALL_TYPES(HttpStatus.BAD_REQUEST, "The group of conditions are invalid."),
    CLIENT_TYPE_INVALID(HttpStatus.BAD_REQUEST, "The client type does not exist"),
    CLIENT_USER_EXISTS(HttpStatus.BAD_REQUEST, "The user exists and must be unique"),
    BAD_UPDATE_REQUEST(HttpStatus.BAD_REQUEST, "User cannot be updated"),
    INSTALLMENTS_OPTIONS_IS_MANDATORY(HttpStatus.CONFLICT, "Installments options is mandatory when type is instalment"),
    VALID_PERIOD_IS_MANDATORY(HttpStatus.CONFLICT, "Valid period dates are mandatory if the benefit is not for the entire event"),
    INVALID_BIN_FORMAT(HttpStatus.BAD_REQUEST, "All bins must be exactly 6 digits"),
    BIN_OR_BRAND_GROUPS_ARE_MANDATORY(HttpStatus.BAD_REQUEST, "BIN groups or brand groups are mandatory for each benefit"),;

    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtCustomersErrorCode(HttpStatus httpStatus, String message) {
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
