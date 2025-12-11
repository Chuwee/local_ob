package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum ApiMgmtPromotionErrorCode implements FormattableErrorCode {

    COLLECTIVE_VALIDATION_LIMIT_REQUIRED(HttpStatus.CONFLICT, "Collective validation limit is required"),
    EVENT_PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Event promotion not found"),
    ENTITY_PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND,
            "Entity promotion not found"),
    EVENT_PROMOTION_NAME_MANDATORY(BAD_REQUEST, "Promotion name is mandatory"),
    EVENT_PROMOTION_INVALID_NAME(BAD_REQUEST, "Invalid promotion name"),
    EVENT_PROMOTION_TYPE_MANDATORY(BAD_REQUEST, "Promotion type is mandatory"),
    EVENT_PROMOTION_EMPTY_TARGET(BAD_REQUEST, "Promotion with type RESTRICTED must contain a list of %s"),
    EVENT_PROMOTION_USED_NAME(CONFLICT, "Promotion name already in use"),
    EVENT_PROMOTION_AUTOMATIC_TYPE_INVALID_PARAMS(HttpStatus.BAD_REQUEST,
            "The request body has invalid automatic promotion params"),
    EVENT_PROMOTION_AUTOMATIC_TYPE_BAD_REQUEST(BAD_REQUEST, "The request body has invalid automatic promotion params"),
    EVENT_PROMOTION_NON_AUTOMATIC_TYPE_INVALID_PARAMS(HttpStatus.BAD_REQUEST,
            "The request body has invalid non automatic promotion params"),
    EVENT_PROMOTION_INVALID_VALUE(HttpStatus.BAD_REQUEST,
            "Promotion discount value must exist and being greater than 0"),
    EVENT_PROMOTION_INVALID_SESSION_ID(HttpStatus.BAD_REQUEST, "One or more sessions ids are invalid"),
    EVENT_PROMOTION_INVALID_RATE_ID(HttpStatus.BAD_REQUEST, "One or more rates ids are invalid"),
    EVENT_PROMOTION_INVALID_PRICE_TYPE_ID(HttpStatus.BAD_REQUEST,
            "One or more price types ids are invalid"),
    EVENT_PROMOTION_INVALID_DISCOUNT_TYPE(HttpStatus.BAD_REQUEST,
            "NO_DISCOUNT type is not applicable for not presale promotions"),
    EVENT_PROMOTION_INVALID_CHANNEL_ID(HttpStatus.BAD_REQUEST,
            "One or more channels ids are invalid"),
    ENABLE_EVENT_PROMOTION_INVALID_CHANNELS_ASSIGNMENT(HttpStatus.CONFLICT,
            "Channel assignment must be defined"),
    ENABLE_EVENT_PROMOTION_INVALID_SESSIONS_ASSIGNMENT(HttpStatus.CONFLICT, "Session assignment must be defined"),
    ENABLE_EVENT_PROMOTION_INVALID_RATES_ASSIGNMENT(HttpStatus.CONFLICT, "Rates assignment must be defined"),
    ENABLE_EVENT_PROMOTION_INVALID_PRICE_TYPES_ASSIGNMENT(HttpStatus.CONFLICT, "Price type assignment must be defined"),
    ENABLE_EVENT_PROMOTION_INVALID_DISCOUNT(HttpStatus.CONFLICT, "Discount must be defined"),
    EVENT_PROMOTION_INVALID_DISCOUNT_VALUE(HttpStatus.BAD_REQUEST,
            "Invalid discount type or value for this kind of promotion."),
    ENABLE_EVENT_PROMOTION_INVALID_VALIDITY_PERIOD(HttpStatus.CONFLICT, "Validity period must be defined"),
    ENABLE_EVENT_PROMOTION_INVALID_CONTENTS(HttpStatus.CONFLICT, "Promotion contents must be defined"),
    EVENT_PROMOTION_LIMIT_SESSION_GREATER_THAN_PROMOTION(HttpStatus.BAD_REQUEST, "Session limit cant be greater than promotion limit"),
    EVENT_PROMOTION_LIMIT_MAX_PURCHASE_GREATER_THAN_PROMOTION(HttpStatus.BAD_REQUEST, "Purchase limit cant be greater than promotion limit"),
    EVENT_PROMOTION_LIMIT_MAX_PURCHASE_GREATER_THAN_SESSION(HttpStatus.BAD_REQUEST, "Purchase limit cant be greater than session limit"),
    EVENT_PROMOTION_LIMIT_MIN_PURCHASE_GREATER_THAN_PROMOTION(HttpStatus.BAD_REQUEST, "Purchase min limit cant be greater than promotion limit"),
    EVENT_PROMOTION_LIMIT_MIN_PURCHASE_GREATER_THAN_SESSION(HttpStatus.BAD_REQUEST, "Purchase min limit cant be greater than session limit"),
    EVENT_PROMOTION_LIMIT_MIN_PURCHASE_GREATER_THAN_MAX_PURCHASE(HttpStatus.BAD_REQUEST, "Purchase min limit cant be greater than purchase max limit"),
    EVENT_PROMOTION_LIMIT_GROUPS_GREATER_THAN_MAX_PURCHASE(HttpStatus.BAD_REQUEST, "Groups limit cant be greater than purchase max limit"),
    EVENT_PROMOTION_LIMIT_MIN_PURCHASE_NOT_PROMOTION_MULTIPLE(HttpStatus.BAD_REQUEST, "Max limit by promotion must be multiple of purchase min"),
    EVENT_PROMOTION_LIMIT_MIN_PURCHASE_NOT_SESSION_MULTIPLE(HttpStatus.BAD_REQUEST, "Max limit by session must be multiple of purchase min"),
    EVENT_PROMOTION_LIMIT_MIN_PURCHASE_NOT_PURCHASE_MULTIPLE(HttpStatus.BAD_REQUEST, "Max limit by purchase must be multiple of purchase min"),
    EVENT_PROMOTION_LIMIT_PURCHASE_MAX_NOT_TICKET_GROUP_MIN_MULTIPLE(HttpStatus.BAD_REQUEST, "Max limit by operation must be multiple of limit groups"),
    EVENT_PROMOTION_LIMIT_PROMOTION_MAX_NOT_TICKET_GROUP_MIN_MULTIPLE(HttpStatus.BAD_REQUEST, "Max limit by promotion must be multiple of limit groups"),
    EVENT_PROMOTION_LIMIT_SESSION_MAX_NOT_TICKET_GROUP_MIN_MULTIPLE(HttpStatus.BAD_REQUEST, "Max limit by session must be multiple of limit groups"),
    EVENT_PROMOTION_BASE_PRICE_RANGES_NOT_VALID(HttpStatus.CONFLICT, "Base price ranges are not valid"),
    EVENT_PROMOTION_BASE_PRICE_VALUE_NOT_VALID(HttpStatus.CONFLICT, "Base price value can not be less than 0"),
    EVENT_PROMOTION_LIMITS_INVALID_STATE(HttpStatus.BAD_REQUEST, "purchase_min and ticket_group_min cannot be enabled twice"),

    EVENT_PROMOTION_COLLECTIVE_INVALID(HttpStatus.BAD_REQUEST, "The request body has invalid collective params"),
    EVENT_PROMOTION_INVALID_COLLECTIVE_ID(HttpStatus.BAD_REQUEST, "Invalid collective id"),
    EVENT_PROMOTION_REQUIRED_COLLECTIVE_ID(HttpStatus.BAD_REQUEST, "Collective Id is required"),
    EVENT_PROMOTION_COLLECTIVE_INVALID_STATE(HttpStatus.CONFLICT, "Collectives must be validated in office box"),
    EVENT_PROMOTION_CAN_NOT_BE_RESTRICTIVE_SALE(HttpStatus.CONFLICT, "Collective type makes promotion unable to be in restrictive sale"),
    EVENT_PROMOTION_PACK_INVALID_PACK_IDS(HttpStatus.CONFLICT,"Pack ids not found for event id"),
    SHOPPING_CART_COLLECTIVE_NOT_COMPATIBLE_WITH_PROMOTIONS(HttpStatus.BAD_REQUEST, "Collective with shopping cart validation cannot be added to a promotion"),

    COLLECTIVE_SELF_MANAGEMENT_NOT_ALLOWED(CONFLICT, "AVET partner validation is not compatible"),
    PROMOTION_CHANNEL_RELATIONSHIP_NOT_SUPPORTED(CONFLICT, "The relationship between the channel and the entity promotion is not supported"),

    CHANNEL_PROMOTION_BAD_REQUEST(HttpStatus.BAD_REQUEST, "%s"),
    CHANNEL_PROMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel promotion not found"),
    CHANNEL_PROMOTION_NAME_EXISTS(HttpStatus.CONFLICT, "Promotion name already exists for this channel"),
    CHANNEL_PROMOTION_COLLECTIVE_TYPE(HttpStatus.CONFLICT, "The request body has invalid collective promotion params"),
    CHANNEL_PROMOTION_AUTOMATIC_TYPE(HttpStatus.CONFLICT, "The request body has invalid automatic promotion params"),
    CHANNEL_PROMOTION_INVALID_DISCOUNT(HttpStatus.CONFLICT, "The request body has invalid discount params"),
    CHANNEL_PROMOTION_INVALID_MIN_AMOUNT_LIMIT(HttpStatus.CONFLICT, "The request body has invalid min amount limit params"),
    CHANNEL_PROMOTION_INVALID_DATES(HttpStatus.CONFLICT, "The request body has invalid date params"),
    CHANNEL_PROMOTION_TYPE_MANDATORY(HttpStatus.BAD_REQUEST, "Promotion type is mandatory"),
    CHANNEL_PROMOTION_AMOUNT_MANDATORY(HttpStatus.BAD_REQUEST, "Amount is mandatory"),
    CHANNEL_PROMOTION_BAD_CONFIG_PACKS_SESSIONS(CONFLICT,
            "The request body has invalid config for packs: Sessions should be equals or greater than num events"),
    CHANNEL_PROMOTION_INVALID_PRICE_TYPE(HttpStatus.CONFLICT, "The request body has one o more invalid price type id"),
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel not found"),
    CHANNEL_TYPE_NOT_SUPPORTED(HttpStatus.CONFLICT, "Channel type not supported"),
    CHANNEL_PROMOTION_COLLECTIVE_TYPE_INVALID(HttpStatus.CONFLICT, "Channel promotion collective type invalid"),
    ENABLE_CHANNEL_PROMOTION_INVALID_EVENTS_ASSIGNMENT(HttpStatus.CONFLICT,
            "Event assignment must be defined"),
    ENABLE_CHANNEL_PROMOTION_INVALID_SESSIONS_ASSIGNMENT(HttpStatus.CONFLICT, "Session assignment must be defined"),
    ENABLE_CHANNEL_PROMOTION_INVALID_DISCOUNT(HttpStatus.CONFLICT, "Discount must be defined"),
    ENABLE_CHANNEL_PROMOTION_INVALID_VALIDITY_PERIOD(HttpStatus.CONFLICT, "Discount must be defined"),
    ENABLE_CHANNEL_PROMOTION_REQUIRED_CONTENTS(HttpStatus.CONFLICT, "Promotion contents must be defined"),
    EVENT_NOT_FOUND_OR_INVALID_EVENT_STATE(HttpStatus.CONFLICT, "Event not found or invalid event state"),
    SESSION_NOT_FOUND_OR_INVALID_SESSION_STATE(HttpStatus.CONFLICT, "Session not found or invalid session state"),
    EVENT_SESSION_CONFLICT(HttpStatus.CONFLICT, "Invalid session id for filtered events"),
    BAD_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "%s"),
    EVENT_PROMOTION_PRESALE_NOT_MODIFIABLE_PARAM(HttpStatus.BAD_REQUEST, "Invalid presale promotion content"),
    PRESALE_PROMOTION_SCOPE_NOT_UPDATABLE(HttpStatus.CONFLICT, "Scope not updatable in presale promotion"),
    RESTRICTIVE_SALE_MANDATORY_FOR_PRESALE_PROMOTION(HttpStatus.CONFLICT, "Restrictive sale is mandatory for presale promotion"),
    PRESALE_PROMO_ON_AVET_EVENT(CONFLICT, "Presale promo not available on AVET event, use selfManaged instead"),
    EVENT_PROMOTION_RATES_ASSIGNMENT_NOT_MODIFIABLE(HttpStatus.CONFLICT, "Event promotion rates cant be upated for presale promotion"),
    EVENT_PROMOTION_PRICE_TYPES_ASSIGNMENT_NOT_MODIFIABLE(HttpStatus.CONFLICT, "Price types cant be upated for presale promotion"),
    EVENT_PROMOTION_ENABLE_DUPLICATE_PRESALE(HttpStatus.CONFLICT, "A session can only have one active presale promo"),
    EVENT_PROMOTION_CHANNEL_ASSIGNMENT_NOT_SUPPORTED(HttpStatus.CONFLICT, "Channel assignment of type ALL is not supported for presale promotion"),
    EVENT_PROMOTION_COLLECTIVE_REQUIRED_FOR_PRESALE(HttpStatus.BAD_REQUEST, "collective.type NONE is not allowed for presale promotions"),
    SESSION_COLLECTIVE_LIMIT_REQUIRES_COLLECTIVE_EXTERNAL_INTERNAL(HttpStatus.CONFLICT, "Session User collective limit requires an external or internal collective bounded to the promotion"),
    SESSION_COLLECTIVE_LIMIT_INCOMPATIBLE_AUTOMATIC(HttpStatus.CONFLICT, "Session user collective limit is not compatible with automatic promotion"),
    EVENT_PROMOTION_BAD_REQUEST(HttpStatus.BAD_REQUEST, "%s"),
    ENTITY_PROMOTION_TEMPLATE_NOT_ACCESIBLE(HttpStatus.FORBIDDEN, "Entity promotion template from different entity than the event"),
    ENTITY_PROMOTION_TEMPLATE_NAME_EXISTS(HttpStatus.CONFLICT, "Promotion name already exists for this entity"),
    CHANNEL_PROMOTION_NOT_AVAILABLE(HttpStatus.CONFLICT, "Channel promotion not available"),
    PRODUCT_PROMOTION_NOT_FOUND(NOT_FOUND, "Product promotion not found"),
    PRODUCT_PROMOTION_BAD_REQUEST(BAD_REQUEST, "%s"),
    PRODUCT_PROMOTION_ACTIVATOR_NOT_SUPPORTED(CONFLICT, "Invalid activator.type"),
    ENTITY_SECONDARY_MARKET_NOT_ACTIVE(HttpStatus.CONFLICT,"Secondary market is not active for this entity"),

    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity not found"),
    MISSING_RATES_IN_PROMOTION_CONDITIONS(HttpStatus.BAD_REQUEST, "Missing rates in promotion conditions"),
    INVALID_RATES_IN_PROMOTION_CONDITIONS(HttpStatus.CONFLICT, "Invalid rates in promotion conditions"),
    INVALID_CUSTOMER_TYPES_IN_PROMOTION_CONDITIONS(HttpStatus.CONFLICT, "Invalid customer types in promotion conditions"),
    CUSTOMER_TYPE_NOT_FOUND_IN_ENTITY(HttpStatus.CONFLICT, "Customer type not found in entity"),
    DUPLICATED_RATES_IN_PROMOTION_CONDITIONS(HttpStatus.CONFLICT, "Duplicated rates in promotion conditions"),
    DUPLICATED_CUSTOMER_TYPES_IN_PROMOTION_CONDITIONS(HttpStatus.CONFLICT, "Duplicated customer types in promotion conditions"),
    EVENT_PROMOTION_CURRENCY_NOT_MATCH(HttpStatus.BAD_REQUEST, "The promotion currency does not match the event currency");

    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtPromotionErrorCode(HttpStatus httpStatus, String message) {
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
        return Stream.of(values()).filter(errorCode -> errorCode.getErrorCode().equals(code)).findFirst().orElse(null);
    }

}
