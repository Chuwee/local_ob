package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;


public enum ApiMgmtSessionErrorCode implements FormattableErrorCode {

    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Session not found"),
    SESSION_NOT_MATCH_EVENT(BAD_REQUEST, "Session is not from this event"),
    INVALID_SESSION_ID(BAD_REQUEST, "Invalid session id"),

    SESSION_NOT_AVET(HttpStatus.NOT_FOUND, "Not an AVET session"),
    AVET_SESSIONS_CANNOT_BE_BULK_CREATED(CONFLICT, "Avet sessions cannot be created in bulk"),
    AVET_SESSIONS_CANNOT_BE_BULK_UPDATED(CONFLICT, "Avet sessions cannot be updated in bulk"),
    SESSION_WITH_BOOKED_SEAT(CONFLICT, "Session with any booked seat"),

    SESSION_START_DATE_NOT_UPDATABLE(BAD_REQUEST, "Start_date cannot be updated"),
    SESSION_END_DATE_NOT_UPDATABLE(BAD_REQUEST, "End_date cannot be updated"),
    SESSION_RATES_NOT_UPDATABLE(BAD_REQUEST, "Rates cannot be updated"),
    SESSION_RATES_LOCKED_BY_CONFIGURED_DYNAMIC_PRICES(BAD_REQUEST, "Rates are locked by configured dynamic prices"),
    INVALID_SESSIONS_TEMPLATE_BULK(BAD_REQUEST, "Sessions with different templates cant be bulk updated"),
    INVALID_NNZ_TEMPLATE_BULK(BAD_REQUEST, "Invalid not numbered zones for template on bulk update"),

    INVALID_SESSION_SPACE(HttpStatus.BAD_REQUEST, "Space is not valid for session"),
    INVALID_SESSION_PACK_TYPE(HttpStatus.BAD_REQUEST, "Session must be of any valid pack type for link/unlink"),
    INVALID_SESSION_DATES_START_REQUIRED(HttpStatus.BAD_REQUEST, "Invalid dates for session: start required"),
    INVALID_SESSION_DATES_END_BEFORE_START(HttpStatus.BAD_REQUEST, "Invalid dates for session: end before after"),
    INVALID_SESSION_DATES_RELEASE_REQUIRED(HttpStatus.BAD_REQUEST, "Invalid dates for session: release required"),
    INVALID_SESSION_DATES_RELEASE_AFTER_START(HttpStatus.BAD_REQUEST, "Invalid dates for session: release after start"),
    INVALID_SESSION_DATES_RELEASE_AFTER_BOOKING_START(HttpStatus.BAD_REQUEST, "Invalid dates for session: release after booking start"),
    INVALID_SESSION_DATES_RELEASE_AFTER_SALES_START(HttpStatus.BAD_REQUEST, "Invalid dates for session: release after sales start"),
    INVALID_SESSION_DATES_SALES_START_REQUIRED(HttpStatus.BAD_REQUEST, "Invalid dates for session: sales start required"),
    INVALID_SESSION_DATES_SALES_START_BEFORE_RELEASE(HttpStatus.BAD_REQUEST, "Invalid dates for session: sales start before release"),
    INVALID_SESSION_DATES_SALES_START_AFTER_START(HttpStatus.BAD_REQUEST, "Invalid dates for session: sales start after start"),
    INVALID_SESSION_DATES_SALES_START_NOT_BEFORE_SALES_END(HttpStatus.BAD_REQUEST, "Invalid dates for session: sales start not before sales end"),
    INVALID_SESSION_DATES_SALES_END_REQUIRED(HttpStatus.BAD_REQUEST, "Invalid dates for session: sales end required"),
    INVALID_SESSION_DATES_SALES_END_BEFORE_BOOKING_END(HttpStatus.BAD_REQUEST, "Invalid dates for session: sales end before booking end"),
    INVALID_SESSION_DATES_BOOKING_START_REQUIRED(HttpStatus.BAD_REQUEST, "Invalid dates for session: booking start required"),
    INVALID_SESSION_DATES_BOOKING_START_BEFORE_RELEASE(HttpStatus.BAD_REQUEST, "Invalid dates for session: booking start before release"),
    INVALID_SESSION_DATES_BOOKING_START_NOT_BEFORE_BOOKING_END(HttpStatus.BAD_REQUEST, "Invalid dates for session: booking start not before booking end"),
    INVALID_SESSION_DATES_BOOKING_END_REQUIRED(HttpStatus.BAD_REQUEST, "Invalid dates for session: booking end required"),
    INVALID_SESSION_DATES_BOOKING_END_AFTER_SALES_END(HttpStatus.BAD_REQUEST, "Invalid dates for session: booking end after sales end"),
    INVALID_SESSION_DATES_BOOKING_END_CONFLICT_EVENT_LIMIT(HttpStatus.BAD_REQUEST, "Invalid dates for session: booking end conflict with event limit"),
    INVALID_IDS_FOR_SESSION(HttpStatus.BAD_REQUEST, "Invalid ids for this session"),
    INVALID_SESSION_TAX(HttpStatus.BAD_REQUEST, "Invalid tax id for this session"),
    SALE_TYPE_MANDATORY(HttpStatus.BAD_REQUEST, "Session sale type is mandatory for activity events"),
    SALE_TYPE_GROUP_FORBIDDEN(HttpStatus.BAD_REQUEST, "Session sale type GROUP is not allowed by event"),
    EVENT_SESSION_PACKS_NOT_ALLOWED(CONFLICT, "The event does not allow the creation of session packs"),
    SESSION_CLONE_GENERATION_STATUS(HttpStatus.BAD_REQUEST, "Session not successfully generated cant be cloned"),
    SESSION_CLONE_SESSION_PACK(HttpStatus.BAD_REQUEST, "Session pack cant be cloned"),
    SESSION_CREATE_SESSION_PACK_STATUS(HttpStatus.BAD_REQUEST, "Linked session status must be PLANNED"),
    SESSION_CREATE_PACK_BLOCKING_REASON(HttpStatus.BAD_REQUEST, "Invalid blocking reason for session template"),
    SESSION_PACK_LINK_SEATS(HttpStatus.BAD_REQUEST, "Link seats to session pack"),
    SEAT_INVALID_STATUS(HttpStatus.CONFLICT, "Session seats with status BLOCKED, BOOKED, INVITATION, ISSUED, SOLD, KILL cannot be linked"),
    EVENT_SESSION_CAPACITY_LOCK(HttpStatus.LOCKED, "Event is updating"),
    SESSION_PACK_UNLINK_SEATS(HttpStatus.BAD_REQUEST, "Unlink seats to session pack"),
    SESSION_PACK_BULK_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "Session pack cannot use bulk operations"),
    SESSION_CREATE_PACK_ALLOW_PARTIAL_REFUND(CONFLICT, "Partial refund only available for unrestricted session pack"),
    SESSION_PARTIAL_REFUND_TIERS_INCOMPATIBLE(CONFLICT, "Partial refund is not compatible with tiers"),
    SESSION_PACK_ALLOW_REFUND_CONDITIONS(CONFLICT, "Session refund conditions only available for unrestricted session pack"),
    SESSION_PACK_NOT_ALLOW_PARTIAL_REFUND(PRECONDITION_FAILED, "Partial refund not allowed for this session pack"),

    SESSION_PACK_REFUND_CONDITIONS_NOT_FOUND(NOT_FOUND,"Session pack refund conditions not found"),
    SESSION_PACK_REFUND_CONDITIONS_INVALID_SESSIONS(CONFLICT,"Invalid sessions for the session pack"),
    SESSION_PACK_REFUND_CONDITIONS_INVALID_PRICETYPES(CONFLICT,"Invalid price types for the session pack"),
    SESSION_PACK_REFUND_CONDITIONS_INVALID_RATES(CONFLICT,"Invalid rates for the session pack"),
    SESSION_PACK_REFUND_CONDITIONS_INVALID_PERCENTAGE(BAD_REQUEST,"Refund percentage values must be among 0 and 100"),
    SESSION_PACK_REFUND_CONDITIONS_INVALID_TOTAL_PERCENTAGE(BAD_REQUEST,"The accumulated percentage by price type and rate must be equals to 100"),
    SESSION_PACK_REFUND_CONDITIONS_INVALID_QUOTA(CONFLICT,"Invalid quota for the session pack"),
    SESSION_PACK_REFUND_CONDITIONS_INVALID_SEAT_STATUS(BAD_REQUEST,"Invalid seat status"),
    SESSION_PACK_REFUND_CONDITIONS_INVALID_BLOCKING_REASON(CONFLICT,"Invalid blocking reason for venue template"),

    INVALID_CART_LIMIT(BAD_REQUEST, "Cart limit must be greater than 0"),
    PRICE_TYPE_NOT_IN_SESSION(BAD_REQUEST, "Price type doesn't belong to session's venue template"),
    INVALID_TARGET_PRICE_TYPE_ID(BAD_REQUEST, "Invalid target price type id - must be array of 1 item"),
    INVALID_MIN_PRICE_TYPE_LIMIT(BAD_REQUEST, "Invalid min value for price type limit"),
    INVALID_MAX_PRICE_TYPE_LIMIT(BAD_REQUEST, "Invalid max value for price type limit"),
    MAX_SMALLER_THAN_MIN(BAD_REQUEST, "Max smaller than min for price type limit"),
    MAX_GREATER_THAN_SESSION_CART_LIMIT(BAD_REQUEST, "Max greater than session cart limit for price type limit"),
    DUPLICATED_PRICE_TYPE_LIMIT(BAD_REQUEST, "A certain price type can only have one limitation"),
    INVALID_SALES_TYPE(BAD_REQUEST, "Invalid sales type"),
    INVALID_SESSION_CLIENT_CONFIGURATION(BAD_REQUEST, "Invalid session client configuration"),
    GROUPS_ACTIVITY_SESSION(BAD_REQUEST, "Group config is only available for activity session"),

    VENUES_WITH_DIFFERENT_OLSON_ID(CONFLICT, "Venues have different olson id, specify one"),
    ATTRIBUTE_INVALID_VALUE(BAD_REQUEST, "Invalid attribute value"),
    ATTRIBUTE_INVALID_ID(BAD_REQUEST, "Invalid attribute id"),
    GATE_ID_INVALID(CONFLICT, "The gate is not from session"),
    ACTIVITY_SESSION_MANDATORY(CONFLICT, "Activity/the park session type is mandatory"),
    QUEUE_ALIAS_MANDATORY(BAD_REQUEST, "Queue alias is mandatory"),

    SESSION_UPDATE_NOT_ALLOWED(FORBIDDEN, "%s update not allowed for this role"),

    INVALID_SESSION_PRESALE_DATES_END_BEFORE_START(HttpStatus.BAD_REQUEST, "Invalid dates for presale: end before after"),
    INVALID_SESSION_PRESALE_DATES_REQUIRED(HttpStatus.BAD_REQUEST, "Invalid dates for presale: startDate and endDate required"),
    SESSION_PRESALE_MEMBER_TICKETS_LIMIT_NOT_UPDATABLE(HttpStatus.BAD_REQUEST, "member_tickets_limit cannot be updated"),
    SESSION_PRESALE_GENERAL_TICKETS_LIMIT_NOT_UPDATABLE(HttpStatus.BAD_REQUEST, "general_tickets_limit cannot be updated"),
    SESSION_PRESALE_ALLOW_MULTI_PURCHASE_NOT_UPDATABLE(HttpStatus.BAD_REQUEST, "multiple_purchase can be updated only for Smart Booking sessions"),
    SESSION_PRESALE_COLLECTIVE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Validator collective not found by specified Id"),
    SESSION_PRESALE_LOYALTY_PROGRAM_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "Loyalty program is not enabled for the entity"),
    SESSION_PRESALE_ENTITY_ID_MANDATORY(HttpStatus.BAD_REQUEST, "Entity id id must be specified in additionalConfig"),

    PRODUCER_ID_NOT_VALID(HttpStatus.FORBIDDEN, "The Producer Id does not belong to the entity of the Event"),
    PRODUCER_ID_DO_NOT_EXIST(BAD_REQUEST, "Producer Id do not exists"),
    PRODUCER_ID_MANDATORY(BAD_REQUEST, "Producer Id is mandatory"),
    SESSION_INVALID_STATUS_FOR_INVOICE_MODIFICATION(BAD_REQUEST, "Session invalid status for invoice data update"),
    INVOICE_PREFIX_ID_MANDATORY(BAD_REQUEST, "Invoice Prefix Id is mandatory"),
    INVOICE_PREFIX_NOT_FOUND(NOT_FOUND, "Invoice prefix not found"),
    INVALID_SESSION_GENERATION_STATUS(HttpStatus.BAD_REQUEST, "Session not successfully generated."),
    EMPTY_COUNTRY_FILTER_LIST(BAD_REQUEST, "Country list is mandatory to activate the filter"),
    SUBSCRIPTION_LIST_ID_NOT_FOUND(NOT_FOUND, "Subscription List not found"),
    ATTENDANT_CONFIG_NOT_ALLOWED(BAD_REQUEST, "Attendants config is not valid"),
    ATTENDANT_CONFIG_AUTOFILL_NOT_ALLOWED( BAD_REQUEST, "Attendants config autofill is compatible only with AVET Events."),

    UPDATE_SESSION_EXTERNAL_BARCODE_WITH_SALES(CONFLICT, "Can not update session external barcodes because the session has sales"),
    NO_SEAT_OR_OCCUPATION_INFO_AVAILABLE(NOT_FOUND, "This session's occupation / seat information is no longer available"),
    
    //Price type restrictions
    REQUIRED_PRICE_TYPE_MANDATORY(BAD_REQUEST, "Required price types are mandatory for this operation"),
    TICKET_NUMBER_EXCLUSION_INPUT(CONFLICT, "You must inform either 'required ticket number' or 'locked ticket number', but not both"),
    PRICE_TYPE_NOT_FOUND(NOT_FOUND, "Price type not found"),
    PRICE_TYPE_RESTRICTION_NOT_FOUND(NOT_FOUND, "Price type restriction not found"),
    OPERATION_NOT_ALLOWED_ON_GRAPHIC_TEMPLATE(BAD_REQUEST, "This operation is not allowed on a Graphic Template"),
    PRICE_TYPE_NOT_IN_VENUE_TEMPLATE(BAD_REQUEST, "Price type doesn't belong to venue template"),
    CIRCULAR_PRICE_TYPE_RESTRICTION(CONFLICT, "You cannot restrict a price type to itself"),
    SESSION_EXTERNAL_BARCODES_MUST_BE_CONFIGURED(CONFLICT, "The session external barcodes must be configured"),
    SESSION_STATUS_NOT_VALID(BAD_REQUEST, "Invalid status for update"),
    SESSION_TAXES_REQUIRED(BAD_REQUEST, "Session taxes are required"),
    RATE_EDITING_BLOCKED(CONFLICT, "Rate editing is blocked for events when both event and session are ready");


    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtSessionErrorCode(HttpStatus httpStatus, String message) {
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

