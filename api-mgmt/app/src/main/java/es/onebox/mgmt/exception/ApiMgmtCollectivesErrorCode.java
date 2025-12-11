package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

public enum ApiMgmtCollectivesErrorCode implements FormattableErrorCode {

    SUPPLIED_COLLECTIVE_NOT_FOUND(BAD_REQUEST, "No collective with supplied collective id"),
    COLLECTIVE_CODE_INVALID(BAD_REQUEST, "Invalid collective code. The code can only have alphanumeric characters."),
    COLLECTIVE_NOT_FOUND(HttpStatus.NOT_FOUND, "Collective not found"),
    COLLECTIVE_NO_SUCH_MANDATORY_FIELDS(BAD_REQUEST, "Mandatory fields are required"),
    COLLECTIVE_MANDATORY(BAD_REQUEST, "Collective id is mandatory when creating channel of type SUBSCRIBERS"),
    COLLECTIVE_ENTITIES_ASSIGNED_TO_COLLECTIVE(CONFLICT, "Entities assigned to collective"),
    COLLECTIVE_ACTIVE_PROMOTION_TEMPLATES_TO_COLLECTIVE(CONFLICT, "Active promotions templates"),
    COLLECTIVE_ENTITY_NOT_ALLOWED(BAD_REQUEST, "Entity assignation not allowed for external collectives"),
    COLLECTIVE_ENTITY_NOT_ALLOWED_FOR_OPERATOR(BAD_REQUEST, "Entity assignation not allowed for Operator"),
    COLLECTIVE_INVALID_TYPE_VALIDATION_METHOD_SET(CONFLICT, "type/validation method combination is not valid"),
    COLLECTIVE_EXTERNAL_VALIDATOR_MANDATORY(BAD_REQUEST, "externalValidator attr is mandatory if EXTERNAL type is present"),
    COLLECTIVE_EXTERNAL_VALIDATOR_PROPERTY_EMPTY(BAD_REQUEST, "externalValidator property cannot have empty values"),
    COLLECTIVE_EXTERNAL_VALIDATOR_NOT_FOUND(CONFLICT, "External validator not found"),
    COLLECTIVE_EXTERNAL_TYPE_NOT_APPLICABLE(CONFLICT, "External type is not applicable for non operator entities"),
    COLLECTIVE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "Collective code not found"),
    COLLECTIVE_CODE_INVALID_VALIDITY_PERIOD_DATES(HttpStatus.BAD_REQUEST, "Validity period with invalid date range"),
    COLLECTIVE_CODE_KEY_MANDATORY_FOR_USER_PASSWORD_VALIDATION(HttpStatus.BAD_REQUEST, "Key is mandatory for the validation type USER_PASSWORD"),
    COLLECTIVE_CODE_INVALID_USAGE_LIMIT_FOR_GIFT_TICKET(HttpStatus.BAD_REQUEST, "The usage limit for GIFT_TICKET codes must be at least 1"),
    COLLECTIVE_CODE_BULK_CREATE_EMPTY_LIST(HttpStatus.BAD_REQUEST, "List of collective codes cannot be empty"),
    COLLECTIVE_CODE_BULK_UPDATE_AT_LEAST_ONE_DATA_FIELD_MANDATORY(HttpStatus.BAD_REQUEST, "At least one data field value must be informed"),
    COLLECTIVE_CODE_BULK_CODES_INVALID(HttpStatus.BAD_REQUEST, "Codes cannot be empty list. Only null or list with size greater than 0 are allowed"),
    COLLECTIVE_INTERNAL_TYPES_CANNOT_HAVE_EXTERNAL_VALIDATORS(BAD_REQUEST, "Internal types cannot have external validators"),
    COLLECTIVE_CODE_ALREADY_USED(PRECONDITION_FAILED, "Collective code already used"),
    SHOW_USAGES_ONLY_COLLECTIVE_INTERNAL(CONFLICT, "Show usages only compatible with internal collectives"),
    SHOW_USAGES_GIFT_TICKET_NOT_COMPATIBLE(CONFLICT, "Gift ticket is not compatible with show usages");

    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtCollectivesErrorCode(HttpStatus httpStatus, String message) {
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
