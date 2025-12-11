package es.onebox.event.exception;

import es.onebox.core.exception.ErrorCode;
import es.onebox.event.events.service.EventTierService;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum MsEventTierErrorCode implements ErrorCode {

    TIER_NAME_MANDATORY(BAD_REQUEST, "Tier name is mandatory"),
    TIER_NAME_MAX_LENGTH(BAD_REQUEST, "Tier name cannot be above 50 characters"),
    TIER_NAME_UNIQUE(BAD_REQUEST, "Tier name must be unique for each price type"),
    TIER_PRICE_MANDATORY(BAD_REQUEST, "Tier price is mandatory"),
    TIER_PRICE_TYPE_MANDATORY(BAD_REQUEST, "Tier price type is mandatory"),
    TIER_PRICE_TYPE_BELONG_TO_EVENT(CONFLICT, "price type of tier must belong to the same event"),
    TIER_START_DATE_MANDATORY(BAD_REQUEST, "Tier start date is mandatory"),
    EVENT_CANNOT_USE_TIER(BAD_REQUEST, "This event cannot configure tiers"),
    TIER_START_DATE_AFTER_EVENT(BAD_REQUEST, "Tier date cannot be after event ends"),
    TIER_DATA_MANDATORY(BAD_REQUEST, "Tier data is mandatory"),
    DEACTIVATE_TIERS_WITH_TIERS_CREATED(BAD_REQUEST, "Once active, use_tiered_pricing cannot be deactivated"),
    USE_TIERS_WITH_RATES(BAD_REQUEST, "Event must have exactly one rate to perform this action"),
    CHANGE_TIERS_WITH_SESSIONS(BAD_REQUEST, "Event can't have any session to perform this action"),
    TIER_START_DATE_ALREADY_EXISTS(CONFLICT, "Cannot have two tiers with the same date for the same price type"),
    USE_TIERS_WITH_GRAPHIC_VENUE_TEMPLATE(CONFLICT, "Cannot use tiers with a graphical venue"),
    TIER_NOT_FOUND(NOT_FOUND, "Could not find any tier with given id"),
    TIER_ID_MANDATORY(BAD_REQUEST, "Tier id is mandatory and must be positive"),
    TIER_PRICE_POSITIVE(BAD_REQUEST, "Tier price must be positive"),
    NO_ON_SALE_TIER_FOR_ENTIRE_EVENT_LIFESPAN(CONFLICT, "At least one price type must have a tier on sale at any point in the event lifespan"),
    INVALID_TIER_DATES(CONFLICT, "Invalid tier dates"),
    EVENT_DATES_NEEDED_TO_ACTIVATE_TIERED_EVENT(CONFLICT, "In order to set to READY a tiered event, event dates are needed"),
    LIMIT_NEGATIVE(BAD_REQUEST, "Limit must be positive"),
    LIMIT_BELOW_ZERO(BAD_REQUEST, "Limit must be above 0"),
    INVALID_SALE_GROUP_LIMIT(BAD_REQUEST, "Invalid sale group limit"),
    SALEGROUP_ID_MANDATORY(BAD_REQUEST, "Sale group id is mandatory"),
    SALEGROUP_NOT_FOUND(BAD_REQUEST, "Sale group not found"),
    SALEGROUP_TEMPLATE_NOT_MATCHING_TIER_TEMPLATE(BAD_REQUEST, "Sale group venue template doesn't match tier venue template"),
    SALE_GROUP_TIER_NOT_FOUND(BAD_REQUEST, "Sale group tier not found"),
    TIER_SALE_GROUP_ALREADY_EXISTS(BAD_REQUEST, "Sale group tier already exists"),
    STOCK_CONDITION_WITHOUT_LIMIT(BAD_REQUEST, "Limit should be set or included in the request when setting condition to stock or date"),
    TIER_LIMIT_REACHED(CONFLICT, "Tier limit reached"),
    TIER_SALE_GROUP_LIMIT_REACHED(CONFLICT, "Tier sale group limit reached"),
    TIER_TRANSLATION_MANDATORY(BAD_REQUEST, "tier translation data is mandatory"),
    INVALID_COMM_ELEM_VALUE(HttpStatus.BAD_REQUEST, "Value is mandatory and must be less than "
            + EventTierService.COMM_ELEM_MAX_VALUE_LENGTH),
    INVALID_COMM_ELEM_LANG(HttpStatus.BAD_REQUEST, "Language format is invalid, should be: 'es_ES'");

    private final HttpStatus httpStatus;
    private final String message;

    MsEventTierErrorCode(HttpStatus httpStatus, String message) {
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
