package es.onebox.common.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;

public enum ApiExternalErrorCode implements ErrorCode {

    // Generic errors
    GENERIC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Generic error, something really wrong happened"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Item not found"),
    BAD_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "Some parameters are incorrect or invalid"),
    PERSISTENCE_ERROR(HttpStatus.BAD_REQUEST, "Error persisting data"),
    FORBIDDEN_RESOURCE(HttpStatus.FORBIDDEN, "Denied for permissions. The user does not have required role"),

    // No Content
    NO_CONTENT(HttpStatus.NO_CONTENT, "No content"),

    // Api-external errors
    PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Promotion not found"),
    PROMOTION_CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Promotion channel not found"),
    START_DATE_AFTER_END_DATE(HttpStatus.PRECONDITION_FAILED, "Start date cannot be after the end date"),
    INVALID_ORDER_STATE(HttpStatus.CONFLICT, "The request could not be completed due to a conflict with the order state"),
    INVALID_ORDER_DATE(HttpStatus.PRECONDITION_FAILED, "This operation can only be performed for future events"),
    INVALID_ORDER_TYPE(HttpStatus.PRECONDITION_FAILED, "This operation is not allowed on this type of order"),
    REQUEST_ALREADY_IN_PROCESS(HttpStatus.TOO_MANY_REQUESTS, "This operation has been already done on the last 2 minutes"),
    INVALID_PRICETYPE_MAPINGS(HttpStatus.BAD_REQUEST, "Invalid price type mappings"),
    INVALID_VENUE_TEMPLATE(HttpStatus.NOT_FOUND, "Venue template has no priceZones"),
    CHANNEL_ID_NOT_FOUND(HttpStatus.PRECONDITION_FAILED, "Channel id not found in oauth token"),
    CHANNEL_NOT_FOUND(HttpStatus.PRECONDITION_FAILED, "Channel not found"),
    CHANNEL_ENTITY_NOT_FOUND(HttpStatus.PRECONDITION_FAILED, "Channel entity not found"),
    CHANNEL_DELIVERY_METHODS_NOT_FOUND(HttpStatus.PRECONDITION_FAILED, "Channel entity not found"),
    WRONG_CHANNEL_STATUS(HttpStatus.PRECONDITION_FAILED, "Channel must be active"),
    WRONG_CHANNEL_TYPE(HttpStatus.PRECONDITION_FAILED, "Operation not allowed for this channel type"),
    WRONG_SALES_REQUEST_STATUS(HttpStatus.PRECONDITION_FAILED, "Sale request must be accepted"),
    WRONG_SERVER_TYPE(HttpStatus.PRECONDITION_FAILED, "Operation not allowed for this email server type"),
    EMAIL_SERVER_CONFIG_INCOMPLETE(HttpStatus.PRECONDITION_FAILED, "The email server configuration is incomplete"),
    INVALID_USERDATA(HttpStatus.PRECONDITION_FAILED, "Retrieved user data is not correct"),
    NOT_MEMBER_USERDATA(HttpStatus.PRECONDITION_FAILED, "Retrieved user data is not a member"),
    BLOCKED_USER(HttpStatus.PRECONDITION_FAILED, "The requesting user has been blocked"),
    INVALID_MEMBER_CLASSIFICATION(HttpStatus.PRECONDITION_FAILED, "Member classification of type %s not allowed on this operation"),
    INVALID_AUTHVENDOR_CONFIG(HttpStatus.PRECONDITION_FAILED, "Channel auth vendor configuration is not correct"),
    AUTHVENDOR_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "Auth vendor configuration not found"),
    INVALID_MEMBER_TYPE(HttpStatus.PRECONDITION_FAILED, "Cannot obtain member type"),
    TICKET_TYPE_ERROR(HttpStatus.PRECONDITION_FAILED, "Cannot obtain ticket type for session %d and discount type %s"),
    SESSION_INFO_ERROR(HttpStatus.NOT_FOUND, "Error %s"),
    INVALID_SESSION_STATE(HttpStatus.PRECONDITION_FAILED, "Session state not valid"),
    ADD_PROMOTION_ERROR(HttpStatus.PRECONDITION_FAILED, "Error %d. Cannot add ticket promotion to shopping cart"),
    SHOPPING_CART_TOKEN_MANDATORY(HttpStatus.BAD_REQUEST, "Shopping cart token is mandatory"),
    FRIEND_CODE_MANDATORY(HttpStatus.BAD_REQUEST, "Friend code is mandatory"),
    MEMBER_ID_MANDATORY(HttpStatus.BAD_REQUEST, "Member ID is mandatory"),
    HTTP_NOTIFICATION_CALL_ERROR(HttpStatus.BAD_REQUEST, "Notification call failed"),
    REQUEST_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "Too many parameters for one request."),
    REQUEST_FAILED(HttpStatus.BAD_REQUEST, "%s"),
    INVALID_PARTNER_CODE(HttpStatus.BAD_REQUEST, "Partner code not found"),
    INVALID_USER(HttpStatus.NOT_FOUND, "User code not found"),
    SEAT_NOT_AVAILABLE(HttpStatus.PRECONDITION_REQUIRED, "Seat state not valid"),
    SEAT_NOT_UPDATED(HttpStatus.BAD_REQUEST, "Seat not updated"),
    AUTH_USER_PERMISSION_INVALID(HttpStatus.FORBIDDEN, "User has not permission"),
    SUBTYPE_NOT_EXIST(HttpStatus.BAD_REQUEST, "Subtype does not exist"),
    EVENT_ID_REQUIRED(HttpStatus.BAD_REQUEST, "Event ID required"),
    EVENT_CHANNEL_SURCHARGES_NOT_FOUND(HttpStatus.BAD_REQUEST, "Event channel surcharges not found"),
    EVENT_CHANNEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "Event channel not found"),
    CLASSIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Member classification not found"),
    INVALID_ADJACENCY_CONFIGURATION(CONFLICT, "Can't allow both breaking and skipping adjacent seats at the same time."),
    QUESTION_ID_ALREADY_EXISTS(CONFLICT, "Question IDs must be different"),
    LANGUAGE_NOT_AVAILABLE(HttpStatus.PRECONDITION_FAILED, "Received language codes are not available in the platform"),
    FV_ZONE_NOT_AVAILABLE(CONFLICT, "Fever zone is not available"),
    EVENT_UPDATE_NOT_AVAILABLE(CONFLICT, "Event update is not yet available"),
    EXTERNAL_WALLET_ERROR(CONFLICT, "Error while forcing avet wallet generation"),

    //From api-distribution,
    ORDER_INVALID_PROMOTION(HttpStatus.NOT_FOUND, "Invalid promotion"),
    ORDER_INVALID_COLLECTIVE(HttpStatus.BAD_REQUEST, "Invalid collective"),
    PARAMETER_REQUIRED(HttpStatus.BAD_REQUEST, "Parameter required"),
    ORDER_ITEMS_NOT_SAME_PRICE_TYPES(CONFLICT, "Items are not in the same price-type"),
    ORDER_ATTENDEE_DATA_INVALID_FORMAT(CONFLICT, "The attendee data format is invalid"),

    //From api-catalog,
    TOO_MANY_RESULTS(HttpStatus.CONFLICT, "There are more than 1 item for filtered request"),
    REQUEST_BODY_INCORRECT(HttpStatus.BAD_REQUEST, "Request body is incorrect"),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type must be application/json"),
    INVALID_PARAM(BAD_REQUEST, "Invalid param"),
    INVALID_PARAM_FORMAT(HttpStatus.BAD_REQUEST, "Invalid '%s' format: '%s'"),
    REQUEST_PARAM_SIZE_TOO_LARGE(HttpStatus.BAD_REQUEST, "Request param size too large."),
    INVALID_TIME_ZONE(HttpStatus.BAD_REQUEST, "Invalid requested Time-Zone"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid authorization token"),
    CHANNEL_ID_REQUIRED(HttpStatus.BAD_REQUEST, "Channel ID required"),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Event not found"),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Session not found"),

    // From api-orders-mgmt
    TOO_MUCH_ELEMENTS_TO_PROCESS(HttpStatus.PRECONDITION_FAILED, "Too much elements to process. Limit your request using the available filters."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order code not found"),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Order item not found"),
    REFUND_NOT_ALLOWED(HttpStatus.CONFLICT, "Refund not allowed"),
    PARTIAL_REFUND_NOT_ALLOWED(HttpStatus.CONFLICT, "Partial refund not allowed by gateway"),
    REIMBURSEMENT_NOT_ALLOWED(HttpStatus.CONFLICT, "Reimbursement not allowed"),
    ORDER_STATE_INVALID(HttpStatus.CONFLICT, "Invalid order state"),
    PRODUCT_STATE_INVALID(HttpStatus.CONFLICT, "Invalid product state"),
    EXPORT_LIMIT_REACHED(HttpStatus.BAD_REQUEST, "Cannot request more than two exportation processes simultaneously."),
    FILTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Filter '%s' not found"),
    EXPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "Export ID not found"),
    EXPORT_WITH_TOO_MANY_RECORDS(HttpStatus.CONFLICT, "Too much elements to export. Limit your request using the available filters."),
    AUTH_ENTITY_TYPE_FILTER_NOT_ALLOWED(HttpStatus.FORBIDDEN, "Entity type is not allowed to use channel_entity_id filter"),
    AUTH_CLIENT_ID_NOT_ALLOWED(HttpStatus.FORBIDDEN, "Invalid Client Id"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "You don't have permission to access on this resource"),

    //From app-rest
    EVENT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Event not available"),
    SESSION_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Session not available"),
    SESSION_NOT_ACCESSIBLE_FROM_ENTITY(HttpStatus.FORBIDDEN, "Unauthorized access to object"),
    SESSION_SEATMAP_NOT_GRAPHIC(HttpStatus.BAD_REQUEST, "Session seatmap not graphic"),
    SESSION_SEATMAP_VIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "Session seatmap view not found"),
    SESSION_SEAT_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "Seat does not belong to channel sale group"),
    INVALID_CHANNEL_CONFIG(HttpStatus.CONFLICT, "Channel configuration is invalid"),
    INVALID_TERMINAL_CONFIG(HttpStatus.CONFLICT, "Terminal configuration is invalid"),
    INVALID_POS_CONFIG(HttpStatus.CONFLICT, "Point of sale configuration is invalid"),
    LOGIN_ERROR(HttpStatus.BAD_REQUEST, "Login error"),
    USER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "User is not active"),
    SHOPPING_CART_EXPIRED_OR_NOT_FOUND(HttpStatus.NOT_FOUND, "Shopping cart expired or not found"),

    ERROR_REST_SHOPPINGCART_SEATS_NOT_AVAILABLE(HttpStatus.PRECONDITION_FAILED, "1053"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "1060"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_CODE_INCORRECT(HttpStatus.NOT_FOUND, "1061"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_USER_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "1062"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_USER_PASSWORD_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "1063"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "1064"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_LIMIT_EVENT_EXCEEDED(HttpStatus.INTERNAL_SERVER_ERROR, "1065"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_MIN_LIMIT_NOT_REACHED(HttpStatus.INTERNAL_SERVER_ERROR, "1066"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_GROUP_NEEDED(HttpStatus.INTERNAL_SERVER_ERROR, "1067"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_LIMIT_SESSION_EXCEEDED(HttpStatus.INTERNAL_SERVER_ERROR, "1068"),
    ERROR_REST_SHOPPINGCART_PROMOTION_OR_DISCOUNT_NOT_APPLICABLE(HttpStatus.INTERNAL_SERVER_ERROR, "1069"),

    // Beppas
    BEPASS_EVENT_NOT_DEFINED(CONFLICT, "Bepass event not defined"),

    //From ms-venue
    VENUE_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue template not found"),
    VENUE_TEMPLATE_PRICE_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Price type not found"),
    VENUE_SECTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Sector not found"),


    // From ms client
    CODE_EXPIRED(HttpStatus.BAD_REQUEST, "The authorization code expired or used more than once"),
    INVALID_CREDENTIALS(HttpStatus.BAD_REQUEST, "Invalid credentials"),
    INVALID_GRANT(HttpStatus.BAD_REQUEST,  "Provided Authorization Grant is invalid"),
    REQUIRED_CODE(HttpStatus.BAD_REQUEST,  "Required parameter not found (code)"),
    REQUIRED_GRANT_TYPE(HttpStatus.BAD_REQUEST,  "Required parameter not found (grant_type)"),
    REQUIRED_REDIRECT_URI(HttpStatus.BAD_REQUEST,  "Required parameter not found (redirect_uri)"),
    REQUIRED_CLIENT(HttpStatus.BAD_REQUEST,  "no client authentication included"),
    UNKNOWN_CLIENT(HttpStatus.BAD_REQUEST,  "unknown client"),
    INVALID_CLIENT_ID(HttpStatus.BAD_REQUEST, "A valid OAuth client could not be found for client_id"),
    INVALID_AUTH_OPTION(HttpStatus.BAD_REQUEST, "No code or friendcode present"),
    CLIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Client not found"),
    ENTITY_ID_MANDATORY(BAD_REQUEST, "Entity ID is mandatory"),
    ENTITY_MODULE_B2B_DISABLED(CONFLICT, "Module B2B is not enabled for the entity"),


    // Fever phone verification errors
    FEVER_OTP_SMS_FAILED(HttpStatus.NOT_FOUND, "SMS could not be sent"),
    FEVER_OTP_VERIFICATION_FAILED(HttpStatus.NOT_FOUND, "OTP verification failed"),
    FEVER_OTP_BLACKLISTED_IP(HttpStatus.TOO_MANY_REQUESTS, "Request IP is blacklisted"),
    FEVER_OTP_MAX_ATTEMPTS(HttpStatus.PRECONDITION_FAILED, "Maximum verification attempts exceeded"),
    FEVER_OTP_INVALID_FORMAT(BAD_REQUEST, "Phone number format is invalid"),
    FEVER_OTP_INVALID_PHONE_NUMBER(BAD_REQUEST, "Phone number cannot be registered"),
    FEVER_OTP_INVALID_PARAMETER(BAD_REQUEST, "Request parameter not valid"),
    FEVER_OTP_UNKNOWN_ERROR(HttpStatus.NOT_FOUND, "An unexpected error occurred"),
    FEVER_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Token invalid"),
    FEVER_HEADERS_MISSING(HttpStatus.INTERNAL_SERVER_ERROR, "Headers not found"),

    //From ms notification
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity not found"),
    CLUB_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "Club config not found"),
    WEBHOOK_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "Webhook configuration not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "No users found for config entityId"),
    INVALID_WEBHOOK_SIGNATURE(HttpStatus.BAD_REQUEST, "The message signature is invalid"),
    FORBIDDEN_USER_FOR_WEBHOOK(HttpStatus.FORBIDDEN, "The given user cannot obtain oauth token"),
    ENTITY_CONFIG_CREATE_CONFLICT(HttpStatus.CONFLICT, "Entity configuration already exists"),
    ENTITY_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity has no configuration to update"),
    CURRENCY_CODE_MANDATORY(CONFLICT, "Currency code Mandatory"),

    // From dispatcher
    AVET_METHOD_HAS_BEEN_LOCKED(HttpStatus.PRECONDITION_FAILED, "Avet method has been locked"),
    AVET_NULL_RESPONSE(HttpStatus.PRECONDITION_FAILED, "Avet null response"),
    AVET_KO_STATUS(HttpStatus.PRECONDITION_FAILED, "Avet KO status"),
    TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "Timeout"),

    // From ms event
    PRESALE_NOT_FOUND(HttpStatus.NOT_FOUND, "Presale not found"),
    AUTOMATIC_SALES_FILE_NOT_FOUND(HttpStatus.PRECONDITION_FAILED, "Automatic sales file not found"),
    ERROR_PROCESSING_SALES(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing sales"),
    ERROR_STOPPED_EXECUTION_AUTOMATIC_SALES(HttpStatus.INTERNAL_SERVER_ERROR, "Stopped execution by hazelcast"),
    ERROR_VOID_PROCESSING_FILE(HttpStatus.PRECONDITION_FAILED, "Void sales list"),
    ERROR_RETRIEVING_AVET_CONFIG(HttpStatus.PRECONDITION_FAILED, "Error retrieving AVET config"),
    INPUT_NULL_VALUES(HttpStatus.PRECONDITION_FAILED, "File has mandatory null values"),
    INPUT_EVENT_NULL_VALUES(HttpStatus.PRECONDITION_FAILED, "The file contains mandatory null values specified by the event configuration: %s"),
    INPUT_CHANNEL_NULL_VALUES(HttpStatus.PRECONDITION_FAILED, "The file contains mandatory null values specified by the channel configuration: %s"),
    ATTENDANT_FIELD_CONFIGURATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Can not find attendant field configuration from event configuration"),
    ORIGINAL_LOCATOR_NULL(HttpStatus.PRECONDITION_FAILED, "Original locator is configured and found null values"),
    SALES_FILE_EMPTY(HttpStatus.PRECONDITION_FAILED, "Sales file is empty"),
    SEAT_ID_NULL(HttpStatus.PRECONDITION_FAILED, "Use seat mappings is configured and found null values"),
    GENERIC_REST_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Generic error, something really wrong happened"),
    ORDER_SEAT_NOT_AVAILABLE(HttpStatus.CONFLICT, "Seat not found or available"),
    PRICE_ZONE_ID_NULL(HttpStatus.PRECONDITION_FAILED, "Price zone values are required in all rows"),
    LANGUAGE_CODE_NULL(HttpStatus.PRECONDITION_FAILED, "Language values are required in all rows"),
    ORDER_ATTENDEE_DATA_REQUIRED(HttpStatus.CONFLICT, "Attendee data is not filled"),
    ORDER_ITEMS_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "Item limit exceeded"),
    NO_ADJACENTS_SEATS_FOUND(HttpStatus.PRECONDITION_FAILED, "No adjacents seats found"),
    ALREADY_EXECUTING_AUTOMATIC_SALES(HttpStatus.CONFLICT, "Already executing automatic sales script"),
    ALREADY_EXECUTING_AUTOMATIC_RENEWALS(HttpStatus.CONFLICT, "Already executing automatic renewals process"),
    EXPORT_UPLOAD_EXCEPTION(HttpStatus.NOT_FOUND, "Error uploading report to S3"),
    AMQP_PUSH_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send message to the queue"),
    AUTOMATIC_SALES_RETRY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Automatic sales retry ERROR"),
    AUTOMATIC_RENEWAL_RETRY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Automatic renewals retry ERROR"),
    RENEWAL_NOT_ENABLED(CONFLICT, "Season ticket renewals are not enabled"),
    AUTOMATIC_RENEWAL_NOT_ENABLED(CONFLICT, "Season ticket automatic renewals are not enabled"),
    ENTITY_IS_NOT_ENTITY_ADMIN(CONFLICT, "Entity must be entity Admin"),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "%s"),
    ERROR_VOID_FILENAME(HttpStatus.PRECONDITION_FAILED, "Void filename"),
    PRICE_ZONE_ID_NOT_EXISTS(HttpStatus.PRECONDITION_FAILED, "Price zones defined in groups %s are not defined in price type list"),
    SECTOR_ID_NOT_EXISTS(HttpStatus.PRECONDITION_FAILED, "Sectors defined in groups %s are not defined in sector list"),
    WRONG_EMAIL_IN_ROWS(HttpStatus.PRECONDITION_FAILED, "Emails format in groups %s are not correct"),
    ERROR_VOID_EMAIL(HttpStatus.PRECONDITION_FAILED, "Email field is void"),
    ERROR_PROCESS_NOT_FOUND(HttpStatus.NOT_FOUND, "There is no process executing"),
    ERROR_PROCESSING_RENEWALS(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing renewals"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "No product found for message received"),
    RENEWAL_WITHOUT_PRICE(HttpStatus.NOT_FOUND, "No price found for renewal"),
    VENDOR_AUTHENTICATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Vendor authenticator not found or is default"),

    // From ms-ticket
    S3_FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "S3 file not found"),
    ORDER_TOTALLY_REFUNDED(HttpStatus.PRECONDITION_FAILED, "Order has been refunded"),
    ORDER_NOT_PAID(HttpStatus.PRECONDITION_FAILED, "Order has not been paid"),
    TICKET_PDF_URL_NOT_GENERATED(HttpStatus.INTERNAL_SERVER_ERROR, "Error while generating PDF URL"),

    // From ms-channel
    CHANNEL_FORM_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel Form not found"),
    INVALID_LANGUAGE(HttpStatus.BAD_REQUEST, "Invalid language"),
    INVALID_CHANNEL_TYPE_EVENT_SALE_RESTRICTIONS(CONFLICT, "Event sale restriction only available for OB_PORTAL channel type"),
    BEPASS_TICKET_CREATION_FAILED(CONFLICT, "An error occurred while creating bepass tickets"),
    BEPASS_TICKET_DEACTIVATION_FAILED(CONFLICT, "An error occurred while deactivating bepass tickets");

    private final HttpStatus httpStatus;
    private final String message;

    ApiExternalErrorCode(HttpStatus httpStatus, String message) {
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
