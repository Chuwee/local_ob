package es.onebox.event.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum MsEventSessionErrorCode implements ErrorCode {
    INVALID_NAME_FORMAT("ME0009", HttpStatus.PRECONDITION_FAILED, "Item name has invalid length or reserved characters"),
    SESSION_NOT_MATCH_EVENT("400ME001", BAD_REQUEST, "Session is not from this event"),
    SESSION_NOT_FOUND("404ME001", HttpStatus.NOT_FOUND, "Session not found"),
    SESSION_ID_INVALID("SESSION_ID_INVALID", BAD_REQUEST, "Session Id should not be not null and greater than 0"),
    SESSION_WITH_BOOKED_SEAT("ME0019", HttpStatus.PRECONDITION_FAILED, "Session with any booked seat"),
    INVALID_SPACE("ME0047", BAD_REQUEST, "Invalid space for session"),
    INVALID_SESSION_DATES_START_REQUIRED("ME0058", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_END_BEFORE_START("ME0095", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_RELEASE_REQUIRED("ME0059", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_RELEASE_AFTER_START("ME0060", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_RELEASE_AFTER_BOOKING_START("ME0061", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_RELEASE_AFTER_SALES_START("ME0062", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_SALES_START_REQUIRED("ME0063", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_SALES_START_BEFORE_RELEASE("ME0064", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_SALES_START_AFTER_START("ME0065", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_SALES_START_NOT_BEFORE_SALES_END("ME0066", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_SALES_END_REQUIRED("ME0067", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_SALES_END_BEFORE_BOOKING_END("ME0068", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_BOOKING_START_REQUIRED("ME0069", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_BOOKING_START_BEFORE_RELEASE("ME0070", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_BOOKING_START_NOT_BEFORE_BOOKING_END("ME0071", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_BOOKING_END_REQUIRED("ME0072", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_BOOKING_END_AFTER_SALES_END("ME0073", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_SESSION_DATES_BOOKING_END_CONFLICT_EVENT_LIMIT("ME0074", HttpStatus.BAD_REQUEST, Constants.INVALID_DATES_FOR_SESSION),
    INVALID_TAX("ME0082", BAD_REQUEST, "Invalid tax for session"),
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", NOT_FOUND, "Entity not found"),
    SALE_TYPE_MANDATORY("ME0090", HttpStatus.BAD_REQUEST, "Session sale type is mandatory for activity events"),
    SALE_TYPE_GROUP_FORBIDDEN("ME0091", HttpStatus.BAD_REQUEST, "Session sale type GROUP is not allowed by event"),
    SESSION_CLONE_GENERATION_STATUS("ME0092", HttpStatus.FORBIDDEN, "Session not successfully generated cant be cloned"),
    SESSION_CLONE_SESSION_PACK("ME0093", HttpStatus.FORBIDDEN, "Session pack cant be cloned"),
    SESSION_CREATE_PACK_STATUS("ME0096", HttpStatus.FORBIDDEN, "Linked session status must be PLANNED"),
    SESSION_CREATE_PACK_BLOCKING_REASON("ME0097", HttpStatus.FORBIDDEN, "Invalid blocking reason for session template"),
    BULK_SEASON_UPDATE_FORBIDEN("ME0099", HttpStatus.FORBIDDEN, "Season cannot be updated via bulk operation"),
    PRICE_TYPE_NOT_IN_SESSION("MES0001", BAD_REQUEST, "Price type doesn't belong to session's venue template"),
    INVALID_MAX_PRICE_TYPE_LIMIT("MES0002", BAD_REQUEST, "Invalid max value for price type limit"),
    INVALID_MIN_PRICE_TYPE_LIMIT("MES0003", BAD_REQUEST, "Invalid min value for price type limit"),
    INVALID_SESSION_CLIENT_CONFIGURATION("MES0009", BAD_REQUEST, "Invalid Session Client configuration"),
    MAX_SMALLER_THAN_MIN("MES0004", BAD_REQUEST, "Max smaller than min for price type limit"),
    MAX_GREATER_THAN_SESSION_CART_LIMIT("MES0005", BAD_REQUEST, "Max greater than session cart limit for price type limit"),
    INVALID_CART_LIMIT("MES0006", BAD_REQUEST, "Cart limit must be at least 1"),
    DUPLICATED_PRICE_TYPE_LIMIT("MES0007", BAD_REQUEST, "A certain price type can only have one limitation"),
    INVALID_SALES_TYPE("MES0008", BAD_REQUEST, "Invalid sales type"),
    RELATIVE_DATES_NOT_ALLOWED("RELATIVE_DATES_NOT_ALLOWED", BAD_REQUEST, "Relative dates not allowed at this point"),
    VENUES_WITH_DIFFERENT_OLSON_ID("VENUES_WITH_DIFFERENT_OLSON_ID", CONFLICT, "Venues have different time zones"),
    HOURS_PERIOD_FILTER_MALFORMED("HOURS_PERIOD_FILTER_BAD_PATTERN", BAD_REQUEST, "Hours period filter is malformed"),
    RANGE_NOT_CORRECT("RANGE_NOT_CORRECT", BAD_REQUEST, "The first hour of the range is after the second one"),
    HEX_COLOR_INCORRECT("HEX_COLOR_INCORRECT", BAD_REQUEST, "The hex code value is incorrect"),
    EVENT_SESSION_PACKS_NOT_ALLOWED("EVENT_SESSION_PACKS_NOT_ALLOWED", METHOD_NOT_ALLOWED, "The event does not allow session packs"),
    GATE_ID_INVALID("GATE_ID_INVALID", BAD_REQUEST, "The gate is not from session"),
    ACTIVITY_SESSION_MANDATORY("ACTIVITY_SESSION_MANDATORY", BAD_REQUEST, "Activity/theme park session type is mandatory"),
    SUBSCRIPTION_LIST_ID_NOT_FOUND("SUBSCRIPTION_LIST_ID_NOT_FOUND", HttpStatus.NOT_FOUND, "Subscription list id not found"),
    PRODUCER_ID_NOT_VALID("PRODUCER_ID_NOT_VALID", HttpStatus.FORBIDDEN, "The Producer Id does not belong to the entity of the Event"),
    PRODUCER_ID_DO_NOT_EXIST("PRODUCER_ID_DO_NOT_EXIST", CONFLICT, "Producer Id do not exists"),
    PRODUCER_NOT_FOUND("PRODUCER_NOT_FOUND", HttpStatus.NOT_FOUND, "Producer not found"),
    SESSION_INVALID_STATUS_FOR_INVOICE_MODIFICATION("SESSION_INVALID_STATUS_FOR_INVOICE_MODIFICATION", BAD_REQUEST, "Session invalid status for invoice data update"),
    INVOICE_PREFIX_ID_MANDATORY("INVOICE_PREFIX_ID_MANDATORY", CONFLICT, "Invoice Prefix Id is mandatory for this producer"),
    INVOICE_PREFIX_NOT_FOUND("INVOICE_PREFIX_NOT_FOUND", NOT_FOUND, "Invoice prefix id not found"),
    EMPTY_COUNTRY_FILTER_LIST("EMPTY_COUNTRY_FILTER_LIST", BAD_REQUEST, "Country list is mandatory to activate the filter"),
    QUEUE_ALIAS_MANDATORY("QUEUE_ALIAS_MANDATORY", BAD_REQUEST, "Queue Alias is needed in order to activate the queue"),
    GROUPS_ACTIVITY_SESSION("GROUPS_ACTIVITY_SESSION", BAD_REQUEST, "Group config is only available for activity session"),
    SESSION_CREATE_PACK_ALLOW_PARTIAL_REFUND("SESSION_CREATE_PACK_ALLOW_PARTIAL_REFUND", CONFLICT, "Partial refund only available for unrestricted season pack"),
    SESSION_PARTIAL_REFUND_TIERS_INCOMPATIBLE("SESSION_PARTIAL_REFUND_TIERS_INCOMPATIBLE", CONFLICT, "Partial refund is not compatible with tiers"),
    SESSION_REFUND_CONDITIONS_NOT_FOUND("SESSION_REFUND_CONDITIONS_NOT_FOUND", NOT_FOUND, "Session refund conditions not found"),
    SESSION_REFUND_CONDITIONS_INVALID_SESSIONS("SESSION_REFUND_CONDITIONS_INVALID_SESSIONS", CONFLICT, "Some sessions are invalid for the session pack"),
    SESSION_REFUND_CONDITIONS_INVALID_PRICETYPES("SESSION_REFUND_CONDITIONS_INVALID_PRICETYPES", CONFLICT, "Some price types are invalid for the session pack"),
    SESSION_REFUND_CONDITIONS_INVALID_RATES("SESSION_REFUND_CONDITIONS_INVALID_RATES", CONFLICT, "Some rates are invalid for the session pack"),
    SESSION_REFUND_CONDITIONS_INVALID_PERCENTAGE("SESSION_REFUND_CONDITIONS_INVALID_PERCENTAGE", CONFLICT, "Percentage values must be among 0 and 100"),
    SESSION_REFUND_CONDITIONS_INVALID_TOTAL_PERCENTAGE("SESSION_REFUND_CONDITIONS_INVALID_TOTAL_PERCENTAGE", CONFLICT, "The accumulate percentage of all sessions by price type and rate must be equals to 100"),
    SESSION_REFUND_CONDITIONS_INVALID_QUOTA("SESSION_REFUND_CONDITIONS_INVALID_QUOTA", CONFLICT, "The quota does not belong to the session pack"),
    SESSION_REFUND_CONDITIONS_INVALID_SEAT_STATUS("SESSION_REFUND_CONDITIONS_INVALID_SEAT_STATUS", CONFLICT, "Invalid seat status"),
    SESSION_REFUND_CONDITIONS_INVALID_BLOCKING_REASON("SESSION_REFUND_CONDITIONS_INVALID_BLOCKING_REASON", CONFLICT, "Invalid blocking reason for venue template"),
    DEFAULT_BLOCKING_REASON_NOT_FOUND("DEFAULT_BLOCKING_REASON_NOT_FOUND", CONFLICT, "None default blocking reason found for the venue template"),
    PRICE_TYPE_NOT_IN_VENUE_TEMPLATE("PRICE_TYPE_NOT_IN_EVENT", BAD_REQUEST, "Price type doesn't belong to venue template"),
    PRICE_TYPE_NOT_FOUND("PRICE_TYPE_NOT_FOUND", NOT_FOUND, "Price type not found"),
    PRICE_TYPE_RESTRICTION_NOT_FOUND("PRICE_TYPE_RESTRICTION_NOT_FOUND", NOT_FOUND, "Price type restriction not found"),
    OPERATION_NOT_ALLOWED_ON_GRAPHIC_TEMPLATE("OPERATION_NOT_ALLOWED_ON_GRAPHIC_TEMPLATE", BAD_REQUEST, "This operation is not allowed on a Graphic Template"),
    REQUIRED_PRICE_TYPE_MANDATORY("REQUIRED_PRICE_TYPE_MANDATORY", BAD_REQUEST, "Required price types are mandatory for this operation"),
    TICKET_NUMBER_EXCLUSION_INPUT("TICKET_NUMBER_EXCLUSION_INPUT", CONFLICT, "You must inform either 'required ticket number' or 'locked ticket number', but not both"),
    CIRCULAR_PRICE_TYPE_RESTRICTION("CIRCULAR_PRICE_TYPE_RESTRICTION", CONFLICT, "You cannot restrict a price type to itself"),
    SESSION_EXTERNAL_BARCODES_MUST_BE_CONFIGURED("SESSION_EXTERNAL_BARCODES_MUST_BE_CONFIGURED", CONFLICT, "The session external barcodes must be configured"),
    SESSION_GENERATION_STATUS_INVALID("SESSION_GENERATION_STATUS_INVALID", CONFLICT, "Invalid generation status change"),
    SMART_BOOKING_NOT_ALLOWED("SMART_BOOKING_NOT_ALLOWED", BAD_REQUEST, "Smart Booking is not allowed for session entity"),
    SESSION_TAG_NOT_FOUND("SESSION_TAG_NOT_FOUND", NOT_FOUND,"Session tag not found"),
    SESSION_SECONDARY_MARKET_ENABLE_REQUIRED("SESSION_SECONDARY_MARKET_ENABLE_REQUIRED", BAD_REQUEST, "Enable secondary market flag is required"),
    SESSION_SECONDARY_MARKET_START_DATE_REQUIRED("SESSION_SECONDARY_MARKET_START_DATE_REQUIRED", BAD_REQUEST, "Secondary market start date is required"),
    SESSION_SECONDARY_MARKET_END_DATE_REQUIRED("SESSION_SECONDARY_MARKET_END_DATE_REQUIRED", BAD_REQUEST, "Secondary market end date is required"),
    INVALID_SECONDARY_MARKET_CONFIG("INVALID_SECONDARY_MARKET_CONFIG", CONFLICT, "Invalid secondary market configuration"),
    SECONDARY_MARKET_START_DATE_AFTER_END_DATE("SECONDARY_MARKET_START_DATE_AFTER_END_DATE", BAD_REQUEST, "Secondary market start date is after end date"),
    GROUPS_INCOMPATIBLE_WITH_DYNAMIC_PRICES("GROUPS_INCOMPATIBLE_WITH_DYNAMIC_PRICES", BAD_REQUEST, "Groups are incompatible with dynamic prices"),
    SESSION_LIMIT_NOT_ALLOWED("SESSION_LIMIT_NOT_ALLOWED", BAD_REQUEST, "Session limit is not allowed");

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    MsEventSessionErrorCode(String errorCode, HttpStatus httpStatus, String message) {
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

    private static class Constants {
        public static final String INVALID_DATES_FOR_SESSION = "Invalid dates for session";
    }
}
