package es.onebox.event.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum MsEventPackErrorCode implements ErrorCode {

    PACK_NOT_FOUND(HttpStatus.NOT_FOUND, "Pack not found"),
    PACK_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Pack item not found"),
    PACK_ITEM_SUBSETS_NOT_FOUND(NOT_FOUND, "Pack item subsets not found"),
    PACK_ITEM_PRICE_TYPES_BAD_REQUEST(BAD_REQUEST, "Pack item bad request"),
    PACK_ITEM_PRICE_TYPES_INVALID_MAPPING(BAD_REQUEST, "Pack item bad request on pricetypes mapping"),
    PACK_ITEM_INVALID_FOR_SUBSETS(CONFLICT, "Pack item invalid for subsets"),
    PACK_RATE_NOT_FOUND(HttpStatus.NOT_FOUND, "Pack rate not found"),
    PACK_CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Pack channel not found"),
    PACK_CHANNEL_CURRENCY_NOT_MATCH(CONFLICT, "Pack currency not match with channel currencies"),
    PACK_CHANNEL_EXISTS(HttpStatus.CONFLICT, "Pack channel relation already exists"),
    PACK_STATUS_INVALID(HttpStatus.NOT_FOUND, "Pack cant be modified with ACTIVE status"),
    PACK_CANNOT_BE_ENABLED_PRODUCT_IS_NOT_ACTIVE(CONFLICT, "Pack cannot be enabled. Product is not active"),
    PACK_CANNOT_BE_ENABLED_PRODUCT_WITHOUT_STOCK(CONFLICT, "Pack cannot be enabled. Product without stock"),
    PACK_CANNOT_BE_ENABLED_PRODUCT_IS_NOT_RELATED(CONFLICT, "Pack cannot be enabled. Product is not related with main item"),
    PACK_CANNOT_BE_ENABLED_EVENT_HAS_NO_PUBLISHED_SESSIONS(CONFLICT, "Pack cannot be enabled. Event has no published sessions"),
    PACK_ITEMS_MUST_HAVE_SAME_CURRENCY(CONFLICT, "Pack items must have the same currency");

    private final HttpStatus httpStatus;
    private final String message;

    MsEventPackErrorCode(HttpStatus httpStatus, String message) {
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

}
