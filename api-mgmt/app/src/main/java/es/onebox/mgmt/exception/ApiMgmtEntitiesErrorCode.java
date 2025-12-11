package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

public enum ApiMgmtEntitiesErrorCode implements FormattableErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "Url not found"),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "Profile not found"),
    DEFAULT_PROFILE_NOT_DELETABLE(HttpStatus.CONFLICT, "Default entity profile cannot be deleted"),
    PROFILE_NAME_MANDATORY(HttpStatus.BAD_REQUEST, "Profile name is required"),
    COUNTRY_NOT_FOUND(HttpStatus.NOT_FOUND, "Country not found"),
    BASE_CATEGORIES_MUST_BE_UNIQUELY_MAPPED(HttpStatus.CONFLICT, "Base categories must be uniquely mapped to custom categories"),
    CURRENT_PASSWORD_IS_NOT_CORRECT(HttpStatus.BAD_REQUEST, "The current password is not correct"),
    PASSWORD_NOT_VALID(HttpStatus.CONFLICT, "The password does not meet the requirements", true),
    ALLOW_DIGITAL_SEASON_TICKET_IN_NOT_AVET_ENTITY(HttpStatus.BAD_REQUEST, "allow_digital_season_ticket is only allowed in AVET entities"),
    ENTITY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Entity with name or short name already exists"),
    ENTITY_HAS_ACTIVE_RELATIONSHIPS(HttpStatus.CONFLICT, "Cannot delete this entity because it is related to active events, channels, users or venues."),
    ENTITY_CUSTOMIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity Customization not found"),
    ENTITY_CUSTOMIZATION_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity Customization tag not found"),
    ENTITY_ALLOW_CUSTOM_REQUIRED(HttpStatus.BAD_REQUEST, "Entity allow-custom tag required"),
    ENTITY_CUSTOMIZATION_REQUIRED(HttpStatus.BAD_REQUEST, "Entity Customization required"),
    INVALID_RECOVER_PASSWORD_TOKEN(HttpStatus.CONFLICT, "Invalid token to recover user password"),
    SHORT_NAME_MUST_NOT_CONTAIN_SPACES(HttpStatus.CONFLICT, "Operator short name must not contain spaces"),
    OPERATOR_EXISTS(HttpStatus.CONFLICT, "Operator already exists"),
    OPERATOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Operator not found"),
    NO_CONFIGURED_BARCODE_VALUES_FOR_ENTITY(HttpStatus.BAD_REQUEST, "This entity has no configured values for external barcodes."),
    B2B_REQUIRES_MULTIEVENT_CART(HttpStatus.BAD_REQUEST, "B2B module can not be active without activating multi-event cart"),
    MULTI_AVET_CART_REQUIRES_MULTIEVENT_CART(HttpStatus.CONFLICT, "Multi AVET cart can not be active without activating multi-event cart"),
    DATE_CANNOT_BE_AFTER_NOW(HttpStatus.BAD_REQUEST, "Date cannot be in the future"),
    PERMISSION_ONLY_MANAGED_BY_SYS_ADMIN(HttpStatus.CONFLICT, "The permission can not be managed by the user"),
    BI_BASIC_USERS_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "The number of basic BI users is greater than the limit"),
    BI_ADVANCED_USERS_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "The number of advanced BI users is greater than the limit"),
    BI_MOBILE_USERS_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "The number of mobile BI users is greater than the limit"),
    USERS_SEARCH_PERMISSIONS_REQUIRES_ROLES(HttpStatus.CONFLICT, "To search users by permissions the roles filter must be present"),
    ROL_NOT_ASSIGNED_TO_USER(HttpStatus.CONFLICT, "The user do not have the required rol assigned"),
    PERMISSION_ALREADY_GRANTED(HttpStatus.CONFLICT, "The user already has the permission"),
    PERMISSION_NOT_ASSIGNED(HttpStatus.CONFLICT, "The user does not have the permission"),
    PERMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Permission not found for the selected role"),
    NUMBER_OF_CURRENT_USERS_EXCEEDS_LIMIT(HttpStatus.CONFLICT, "The number of current users exceeds the new limit"),
    ONEBOX_INVOICING_EXPORT_LIMIT_REACHED(HttpStatus.CONFLICT, "The number of concurrent export has been reached"),
    ONLY_ONE_EMAIL_PER_BI_USER(HttpStatus.CONFLICT, "Only a single email is allowed per BI_USER master permission"),
    ROLE_BI_REQUIRES_PERMISSIONS(HttpStatus.CONFLICT, "The BI_USR role requires permissions"),
    BI_USR_PERMISSIONS_NOT_MODIFIABLE_IN_ROLES_ENDPOINT(HttpStatus.CONFLICT, "BI_USR permissions cannot be change with general roles endpoint"),
    BI_INHERIT_PERMISSION_CANNOT_BE_GRANTED(HttpStatus.CONFLICT, "BI_INHERIT permission cannot be granted through API"),
    BI_INHERIT_PERMISSION_CANNOT_BE_MODIFIED(HttpStatus.CONFLICT, "BI_INHERIT permission cannot be modified through API"),
    CANNOT_ASSIGN_BASIC_ADVANCED_PERMISSION(HttpStatus.CONFLICT, "Basic or advanced permission cannot be assigned when basic or advanced is already assigned"),
    FILTERING_ONLY_AVAILABLE_IN_FILTERED_TYPE(HttpStatus.BAD_REQUEST, "Entities and operators filtering only available in filtered type"),
    OPERATOR_REDUNDANT_IN_ENTITY(HttpStatus.BAD_REQUEST, "Operator list and entities list cannot match"),
    VISIBILITY_OPERATORS_CAN_ONLY_SHARED_RESOURCES(HttpStatus.BAD_REQUEST, "Operators visibility of entity can only be SHARED_RESOURCES"),
    ENTITY_IS_OPERATOR(HttpStatus.BAD_REQUEST, "There is a operator in entity list"),
    OPERATOR_IS_ENTITY(HttpStatus.BAD_REQUEST, "There is an entity in operators list"),
    OPERATORS_VISIBILITIES_INCOMPATIBLE_WITH_TYPE(HttpStatus.BAD_REQUEST, "Operators visibilities is incompatible with type"),
    MICROSTRATEGY_AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "Error authenticating microstategy user"),
    BI_SERVER_LOCKED(HttpStatus.CONFLICT, "The BI server is currently locked"),
    BI_ROLE_REQUIRED(HttpStatus.CONFLICT, "BI role is required"),
    BI_PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "MSTR BI project not found"),
    MSTR_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "Microstrategy user not found"),
    MSTR_SECURITY_FILTER_NOT_FOUND(HttpStatus.NOT_FOUND, "MSTR Security Filter not found for user"),
    MSTR_USER_HAS_SUBSCRIPTIONS(HttpStatus.CONFLICT, "Mstr User has Subscriptions"),
    EMAIL_NOT_VALID_FOR_PERMISSION(HttpStatus.CONFLICT, "Email not valid for permission"),
    ENTITY_ADMIN_CAN_NOT_MANAGE_ANOTHER_ENTITY_ADMIN(HttpStatus.CONFLICT, "An entity admin can not manage another entity admin"),
    ENTITY_ADMIN_ONLY_IN_CREATION(HttpStatus.CONFLICT, "You can only use entity admin in create"),
    ENTITY_ADMIN_CAN_NOT_CHANGE_TYPE(HttpStatus.CONFLICT, "You can not change an entity admin type"),
    ENTITY_WITH_AN_ENTITY_ADMIN_CAN_NOT_BE_DELETED(HttpStatus.CONFLICT, "An entity with an entity admin can not be deleted"),
    ROLE_BI_NOT_BULK(HttpStatus.BAD_REQUEST, "BI Role not compatible with update bulk"),
    COOKIE_INTEGRATION_REQUIRES_CONDITIONS_AGREEMENT(HttpStatus.BAD_REQUEST, "Conditions agreement is required in order to integrate our cookies into your solution"),
    ENTITY_ACCOMMODATIONS_CONFIG_NOT_PROPERLY_ENABLED(BAD_REQUEST, "ChannelEnablingMode and at least one vendor and one channel are required in order to enable an accommodations integration"),
    ONLY_CHANNEL_AND_EVENT_ENTITIES_MAY_ENABLE_ACCOMMODATIONS(BAD_REQUEST, "Only Channel & Event entities may enable accommodations integration"),
    INVALID_CHANNEL_LIST(HttpStatus.BAD_REQUEST, "There are invalid channel ID's in the list"),
    CURRENCY_ALREADY_IN_OPERATOR(HttpStatus.CONFLICT, "Currency Alredy in operator"),
    REQUESTED_VISIBILITY_TO_TOO_MANY_ENTITIES(HttpStatus.BAD_REQUEST, "Your request overflowed this entity's fixed limit."),
    B2B_MODULE_NOT_ENABLED(BAD_REQUEST, "Enabled B2B module is required in order to enable B2B publishing"),
    INVITATIONS_REQUIRES_CHANNEL_TYPE(BAD_REQUEST, "Only entities with channel type can enable invitations"),
    COOKIE_INTEGRATION_REQUIRES_CHANNEL_ENABLING_MODE(BAD_REQUEST, "a channel selection mode is required when enabling custom integration"),
    CHANNEL_LIST_NULL_OR_EMPTY(BAD_REQUEST, "a mandatory channel list is either null or empty"),
    DEFAULT_CURRENCY_NOT_SET(HttpStatus.CONFLICT, "Operator default currency not set"),
    PRODUCT_LANGUAGE_DEFAULT_REQUIRED(HttpStatus.PRECONDITION_FAILED, "One product language must be the default value"),
    PRODUCT_EVENT_DELIVERY_POINT_DEFAULT_REQUIRED(HttpStatus.PRECONDITION_FAILED, "One product event delivery point must be the default value"),
    PRODUCT_SESSION_DELIVERY_POINT_DEFAULT_REQUIRED(HttpStatus.PRECONDITION_FAILED, "One product session delivery point must be the default value"),
    USER_REALM_INVALID_RESOURCE(CONFLICT, "Resources not valid"),
    TERMINAL_NOT_FOUND(HttpStatus.NOT_FOUND, "Terminal not found"),
    TERMINAL_CANNOT_BE_CREATED(INTERNAL_SERVER_ERROR, "Error creating terminal with code %s"),
    TERMINAL_CODE_ALREADY_EXISTS(CONFLICT, "Terminal code already exist"),
    TERMINAL_CODE_CONTAINS_WHITESPACE(BAD_REQUEST, "Terminal code cannot contain whitespaces"),
    TERMINAL_CODE_INVALID_NUMBER(BAD_REQUEST, "Terminal code must be a valid number"),
    INVALID_DONATION_PROVIDER_ID(HttpStatus.BAD_REQUEST, "Some provider id is incorrect"),
    PRD_ANS_MUST_BE_LINKED_TO_PRODUCER(BAD_REQUEST, "The Producer Analyst Role must be linked to a Producer"),
    PRD_ANS_CAN_ONLY_HAVE_BI_ROLE(BAD_REQUEST, "The Producer Analyst Role cannot have other roles different than BI"),
    PRD_ANS_LIMITED_TO_BI_BASIC(BAD_REQUEST, "The Producer Analyst Role can only have a BI_BASIC permission"),
    USER_RATE_LIMIT_INVALID_CONFIG(CONFLICT, "Some quota are wrong defined"),
    DEACTIVATION_NOT_ALLOWED(CONFLICT, "Deactivation not allowed"),
    ACTIVATION_LOYALTY_POINTS_NOT_ALLOWED(CONFLICT, "Activation loyalty points is not allowed if the points-currency conversion is not filled in"),
    EVENT_CHANNEL_DIFFERENT_ENTITIES(BAD_REQUEST, "Event and channel enties are different"),
    CURRENCIES_NOT_VALID(BAD_REQUEST, "The currencies do not match all of the operator's currencies"),
    CUSTOMERTYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer type not found"),
    INVALID_CUSTOMERTYPE_CODE(BAD_REQUEST, "Customer type invalid code"),
    CUSTOMERTYPE_CODE_ALREADY_EXISTS(CONFLICT, "Customer type code already exists"),
    INVALID_MEMBER_COUNTER_UPDATE(CONFLICT, "Entity member counter must be greater than its previous value"),
    ENTITY_UNSUPPORTED_LANGUAGE(BAD_REQUEST, "Unsupported entity language"),
    FEVER_ZONE_NOT_ALLOWED_BY_OPERATOR(CONFLICT, "AllowFeverZone is not allowed by operator"),
    FEVER_ZONE_NOT_ALLOWED_BY_ENTITY(CONFLICT, "AllowFeverZone is not allowed by entity"),
    EXTERNAL_REFERENCE_NOT_SET(CONFLICT, "External reference has not been set yet"),
    PRODUCER_HAS_ACTIVE_EVENTS(CONFLICT, "Producer cannot be disabled with existing events active"),
    PRODUCER_HAS_ACTIVE_PRODUCTS(CONFLICT, "Producer cannot be disabled with existing products active"),
    INVALID_PHONE_VALIDATOR_METHOD(BAD_REQUEST, "Invalid Phone validator method"),
    ENTITY_CONFIG_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity config not found"),
    ENTITY_DOMAIN_SETTINGS_NOT_FOUND(HttpStatus.NOT_FOUND, "Entity domain settings not found"),
    BANK_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Bank account not found"),
    TEMPLATE_ZONE_CODE_ALREADY_EXISTS(CONFLICT, "Template zone code already exists"),
	TEMPLATE_ZONE_CANNOT_ENABLED_WITHOUT_COMM_ELEMENTS(CONFLICT, "Template zone cannot be enabled without communication elements"),
    COUNTRY_CODE_REQUIRED(BAD_REQUEST, "Country code has to be filled when setting external invoice enabled"),
    COUNTRY_CODE_NOT_MODIFIABLE(BAD_REQUEST, "Country code cannot be modified after setting the external invoice enabled"),
    ENTITY_CANNOT_ADD_CONFIGURATIONS(PRECONDITION_FAILED, "Entity cannot add configurations");

    private final HttpStatus httpStatus;
    private final String message;

    private final boolean forwardMessage;

    ApiMgmtEntitiesErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.forwardMessage = false;
    }

    ApiMgmtEntitiesErrorCode(HttpStatus httpStatus, String message, Boolean forwardMessage) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.forwardMessage = forwardMessage;
    }

    @Override
    public boolean forwardMessage() {
        return forwardMessage;
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
