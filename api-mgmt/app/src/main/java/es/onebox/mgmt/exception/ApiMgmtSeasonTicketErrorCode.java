package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

public enum ApiMgmtSeasonTicketErrorCode implements FormattableErrorCode {

    RENEWALS_SEATS_NOT_AVAILABLE(HttpStatus.PRECONDITION_FAILED, "Session has not available seats to renewal"),
    SEASON_TICKET_RENEWAL_DIFFERENT_VENUES(HttpStatus.PRECONDITION_FAILED, "Renewal season tickets must have same venue"),
    SEASON_TICKET_RENEWAL_DIFFERENT_ENTITY(HttpStatus.PRECONDITION_FAILED, "Renewal season tickets must have same entity"),
    SEASON_TICKET_RENEWAL_ON_SALE(HttpStatus.PRECONDITION_FAILED, "Renewal season tickets can't be on sale"),
    SEASON_TICKET_RENEWAL_DIFFERENT_MEMBER_MANDATORY(HttpStatus.PRECONDITION_FAILED, "Renewal season tickets must have same member mandatory"),
    SEASON_TICKET_RENEWAL_SAME_SEASON_TICKETS(HttpStatus.CONFLICT, "Renewal season tickets must be different season tickets"),
    SEASON_TICKET_RENEWAL_NOT_READY(HttpStatus.PRECONDITION_FAILED, "Renewal season tickets generation must be ready"),
    SEASON_TICKET_RENEWAL_IN_PROGRESS(HttpStatus.CONFLICT, "Season ticket has a renewal in progress"),
    SEASON_TICKET_RENEWAL_NO_PRODUCTS_TO_RENEWAL(HttpStatus.PRECONDITION_FAILED, "Selected season ticket has not products to renew"),
    SEASON_TICKET_RENEWAL_NOT_FOUND(HttpStatus.NOT_FOUND, "Season ticket renewal not found"),
    SEASON_TICKET_RENEWAL_DELETE_RENEWED_NOT_ALLOWED(HttpStatus.PRECONDITION_FAILED, "Season ticket renewal can't be deleted when is Renewed"),
    SEASON_TICKET_RENEWAL_DELETE_NOT_ALLOWED(HttpStatus.PRECONDITION_FAILED, "Deletion of this renewal is not allowed"),
    SEASON_TICKET_RATE_USED_ON_RENEWAL(HttpStatus.PRECONDITION_FAILED, "This rate is used on a renewal for this Season Ticket"),
    SEASON_TICKET_CHANGE_SEAT_IS_NOT_ALLOWED(HttpStatus.CONFLICT, "Season ticket change seat is not allowed"),
    SEASON_TICKET_CHANGE_SEAT_FIELDS_NOT_INFORMED(HttpStatus.CONFLICT, "Season ticket change seat has fields not informed"),
    SEASON_TICKET_CHANGE_SEAT_PUBLISHING_NOT_ENABLED(HttpStatus.CONFLICT, "Season ticket change seat can't be enabled until publishing is not enabled"),
    SEASON_TICKET_CHANGE_SEAT_PUBLISHING_DATES(HttpStatus.CONFLICT, "Season ticket change seat dates can't be before publishing date");

    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtSeasonTicketErrorCode(HttpStatus httpStatus, String message) {
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
