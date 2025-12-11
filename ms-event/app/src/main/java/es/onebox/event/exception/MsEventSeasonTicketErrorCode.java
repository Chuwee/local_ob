package es.onebox.event.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

public enum MsEventSeasonTicketErrorCode implements ErrorCode {

    SEASON_TICKET_INVALID_NAME_CONFLICT("ME0025", HttpStatus.CONFLICT, "Season ticket name already exists for entity"),
    SEASON_TICKET_NOT_FOUND("ME0026", HttpStatus.NOT_FOUND, "Season ticket not found"),
    INVALID_SEASON_TICKET_ID("ME0027", BAD_REQUEST, "Season ticket id is invalid"),
    SEASON_TICKET_INVALID_NUMBER_OF_SESSIONS("ME0032", HttpStatus.CONFLICT, "Season ticket must have only one session"),
    SEASON_TICKET_MAX_BUYING_LIMIT_RANGE("ME0048", HttpStatus.BAD_REQUEST, "Season ticket max buying limit out of range"),
    SEASON_TICKET_IN_CREATION("ME0077", CONFLICT, "The operation cannot be done until the season ticket is ready"),
    SEASON_TICKET_WITH_BOOKED_SEAT("ME0078", CONFLICT, "Season ticket with any booked seat"),
    SEASON_TICKET_WITH_BOOKED_SEAT_REGISTRY_CHANGE("ME0100", CONFLICT, "Season ticket with any booked seat can't change registry policy"),
    SEASON_TICKET_INVALID_RENEWAL_DATES("ME0103", CONFLICT, "Season ticket renewal dates are invalid"),
    SEASON_TICKET_RENEWAL_IS_NOT_ALLOWED("ME0104", CONFLICT, "Season ticket renewal is not allowed"),
    SEASON_TICKET_RENEWAL_FIELDS_NOT_INFORMED("ME0105", CONFLICT, "Season ticket renewal has fields not informed"),
    SEASON_TICKET_RENEWAL_PUBLISHING_NOT_ENABLED("ME0106", CONFLICT, "Season ticket renewal can't be enabled until publishing is not enabled"),
    SEASON_TICKET_RENEWAL_PUBLISHING_DATES("ME0107", CONFLICT, "Season ticket renewal dates can't be before publishing date"),
    SEASON_TICKET_INVALID_CHANGE_SEAT_DATES("SEASON_TICKET_INVALID_CHANGE_SEAT_DATES", CONFLICT,
            "Season ticket change seat dates are invalid"),
    SEASON_TICKET_INVALID_CHANGE_SEAT_MAX_VALUE("SEASON_TICKET_INVALID_CHANGE_SEAT_MAX_VALUE", CONFLICT,
            "Season ticket maximum change seat value is invalid"),
    SEASON_TICKET_CHANGE_SEAT_IS_NOT_ALLOWED("SEASON_TICKET_CHANGE_SEAT_IS_NOT_ALLOWED", CONFLICT, "Season ticket change seat is not allowed"),
    SEASON_TICKET_CHANGE_SEAT_FIELDS_NOT_INFORMED("SEASON_TICKET_CHANGE_SEAT_FIELDS_NOT_INFORMED", CONFLICT, "Season ticket change seat has fields not informed"),
    SEASON_TICKET_CHANGE_SEAT_PUBLISHING_NOT_ENABLED("SEASON_TICKET_CHANGE_SEAT_PUBLISHING_NOT_ENABLED", CONFLICT, "Season ticket change seat can't be enabled until publishing is not enabled"),
    SEASON_TICKET_CHANGE_SEAT_PUBLISHING_DATES("SEASON_TICKET_CHANGE_SEAT_PUBLISHING_DATES", CONFLICT, "Season ticket change seat dates can't be before publishing date"),
    RENEWALS_SEATS_NOT_AVAILABLE("ME0108", HttpStatus.PRECONDITION_FAILED, "Session has not available seats to renewal"),
    SEASON_TICKET_RENEWAL_DIFFERENT_VENUES("ME0109", HttpStatus.PRECONDITION_FAILED, "Renewal season tickets must have same venue"),
    SEASON_TICKET_RENEWAL_DIFFERENT_ENTITY("ME0110", HttpStatus.PRECONDITION_FAILED, "Renewal season tickets must have same entity"),
    SEASON_TICKET_RENEWAL_ON_SALE("ME0111", HttpStatus.PRECONDITION_FAILED, "Renewal season tickets can't be on sale"),
    SEASON_TICKET_RENEWAL_DIFFERENT_MEMBER_MANDATORY("ME0112", HttpStatus.PRECONDITION_FAILED, "Renewal season tickets must have same member mandatory"),
    SEASON_TICKET_RENEWAL_SAME_SEASON_TICKETS("ME0113", CONFLICT, "Renewal season tickets must be different season tickets"),
    SEASON_TICKET_RENEWAL_NOT_READY("ME0114", PRECONDITION_FAILED, "Renewal season tickets generation must be ready"),
    SEASON_TICKET_RENEWAL_IN_PROGRESS("ME0115", CONFLICT, "Season ticket has a renewal in progress"),
    SEASON_TICKET_RENEWAL_NO_PRODUCTS_TO_RENEWAL("ME0116", PRECONDITION_FAILED, "Selected season ticket has not products to renew"),
    SEASON_TICKET_RENEWAL_NOT_FOUND("ME0117", NOT_FOUND, "Season ticket renewal not found"),
    SEASON_TICKET_RENEWAL_DELETE_RENEWED_NOT_ALLOWED("ME0118", PRECONDITION_FAILED, "Season ticket renewal can't be deleted when is Renewed"),
    SEASON_TICKET_RENEWAL_DELETE_NOT_ALLOWED("ME0119", PRECONDITION_FAILED, "Deletion of this renewal is not allowed"),
    SEASON_TICKET_RATE_USED_ON_RENEWAL("ME0120", PRECONDITION_FAILED, "This rate is used on a renewal for this Season Ticket");


    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    MsEventSeasonTicketErrorCode(String errorCode, HttpStatus httpStatus, String message) {
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
}
