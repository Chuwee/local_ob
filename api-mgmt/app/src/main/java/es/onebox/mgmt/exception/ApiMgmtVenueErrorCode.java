package es.onebox.mgmt.exception;

import es.onebox.core.exception.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

public enum ApiMgmtVenueErrorCode implements FormattableErrorCode {

    BAD_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "Some parameters are incorrect or invalid"),
    BLOCKING_REASON_NOT_FOUND(HttpStatus.NOT_FOUND, "Blocking reason not found"),
    INVALID_TAG_ID(HttpStatus.EXPECTATION_FAILED, "The value of gateId is not valid for this priceTypeId"),
    DEFAULT_GATE_RESTRICTION(HttpStatus.CONFLICT, "The default gateId cannot be modified to not default because you need to set another default gateId before"),
    VENUE_ID_MANDATORY(HttpStatus.BAD_REQUEST, "venueId must be a positive integer"),
    VENUE_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue not found"),
    VENUE_TEMPLATE_CAPACITY_NOT_MODIFIABLE(HttpStatus.CONFLICT, "Event venue template capacity not modifiable. Invalid event status"),
    VENUE_TEMPLATE_CAPACITY_UNSUPPORTED_OPERATION(HttpStatus.CONFLICT, "Event venue template capacity not modifiable. Invalid template type"),
    VENUE_TEMPLATE_CAPACITY_INVALID_PARAM(HttpStatus.BAD_REQUEST, "Event venue template capacity must be increased"),
    VENUE_TEMPLATE_SESSION_VIEW_INVALID_PARAM(HttpStatus.BAD_REQUEST, "Invalid session_id"),
    VENUE_TEMPLATE_SESSION_VIEW_UNSUPPORTED_ENTITY_OPERATION(HttpStatus.CONFLICT, "Session venue template views only must be entity enabled"),
    VENUE_TEMPLATE_SESSION_VIEW_UNSUPPORTED_OPERATION(HttpStatus.CONFLICT, "Session venue template views only available for EVENT scope"),
    INVALID_COMM_ELEM_VALUE(HttpStatus.BAD_REQUEST, "The value of communication element is not valid"),
    VENUE_TEMPLATE_INVALID_PARAM(HttpStatus.CONFLICT, "%s"),
    GATE_NAME_ALREADY_IN_USE(HttpStatus.CONFLICT, "The gate name is already in use"),
    GATE_CODE_ALREADY_IN_USE(HttpStatus.CONFLICT, "The gate code is already in use"),
    PRICE_ZONE_NAME_ALREADY_IN_USE(HttpStatus.CONFLICT, "The price zone name is already in use"),
    PRICE_ZONE_CODE_ALREADY_IN_USE(HttpStatus.CONFLICT, "The price zone code is already in use"),
    INVALID_COMM_ELEM_LANG(HttpStatus.BAD_REQUEST, "Language format is invalid, should be: 'es_ES'"),
    INVALID_COMM_ELEM_TYPE(HttpStatus.BAD_REQUEST, "Unsupported communication element type"),
    PRICE_ZONE_COMMUNICATION_ELEMENT_NOT_DELETED(HttpStatus.LOCKED, "Price zone communication element could not be deleted"),
    PRICE_ZONE_COMMUNICATION_ELEMENT_NOT_UPDATED(HttpStatus.LOCKED, "Price zone communication element could not be updated"),
    PRICE_TYPE_TRANSLATION_MANDATORY(HttpStatus.BAD_REQUEST, "price type translation data is mandatory"),
    PRICE_ZONE_COMMUNICATION_ELEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Price type communication element not found"),
    ENTITY_IS_NOT_VENUE_OWNER(HttpStatus.FORBIDDEN, "The entity is not the owner of this venue"),
    UNDELETABLE_REFERENCED_VENUE(HttpStatus.PRECONDITION_FAILED, "This venue cannot be safely deleted. Either spaces, configurations, templates or salepoints are attached to it."),
    VENUE_NAME_IN_USE(HttpStatus.CONFLICT, "This name is already in use by another venue."),
    ADDRESS_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "The address must contains country, province, city, zip code, latitude and longitude"),
    VENUE_TEMPLATE_VIEW_NAME_CONFLICT(HttpStatus.CONFLICT, "View name already exists within the same venue template"),
    VENUE_TEMPLATE_VIEW_CODE_CONFLICT(HttpStatus.CONFLICT, "View code already exists within the same venue template"),
    INVALID_PARAM(HttpStatus.BAD_REQUEST, "The request is invalid due to missing or incorrect parameters. Check the api definition for further information."),
    OPERATOR_WITHOUT_ENTITYID(HttpStatus.BAD_REQUEST, "Operators must provide an entity ID"),
    ENTITYID_MANDATORY(HttpStatus.BAD_REQUEST, "entity_id is mandatory"),
    UNACCESSIBLE_CALENDAR(HttpStatus.FORBIDDEN, "Can't access another entity's calendar."),
    CALENDAR_NOT_FOUND(HttpStatus.BAD_REQUEST, "No calendar found by this ID."),
    VENUE_CONTAINS_NO_SUCH_SPACE(HttpStatus.BAD_REQUEST, "The ID of the requested space does not exist in this venue."),
    SPACE_NAME_IN_USE(HttpStatus.CONFLICT, "This name is already in use by another space in the same venue."),
    UNDELETABLE_DEFAULT_VENUE_SPACE(HttpStatus.CONFLICT, "The default space of a venue cannot be deleted"),
    VENUE_WITHOUT_DEFAULT_SPACE_NOT_ALLOWED(HttpStatus.CONFLICT, "It is not allowed to unset a default space: Venues require to have one always. Set another default space to unset this one."),
    LATITUDE_COORDINATE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Latitude is out of the valid range (from -90.0 to 90.0)"),
    LONGITUDE_COORDINATE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "Longitude is out of the valid range (from -180.0 to 180.0)"),
    ENTITY_IS_NOT_AVET(HttpStatus.BAD_REQUEST, "The type is AVET but the entity is not AVET"),
    VENUE_TEMPLATE_AVET_CAPACITY_ID_MANDATORY(HttpStatus.BAD_REQUEST, "The external capacity ID is mandatory when the venue template type is AVET"),
    VENUE_TEMPLATE_AVET_CAPACITY_ID_ENTITY_MISMATCH(HttpStatus.BAD_REQUEST, "The provided external capacity ID is from a different AVET entity than the provided entity ID"),
    VENUE_TEMPLATE_AVET_INITIALIZATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error was produced on the initialization of the AVET venue template"),
    VENUE_TEMPLATE_AVET_NOT_ALLOWED(HttpStatus.FORBIDDEN, "VenueTemplate type AVET is not allowed for this operation"),
    VENUE_TEMPLATE_ID_CONFLICT(HttpStatus.CONFLICT, "At least one of the ID fields of the body does not belong to this venue template"),
    VENUE_TEMPLATE_WITHOUT_DEFAULT_TAG(HttpStatus.CONFLICT, "Missing mandatory default tag for venue template"),
    VENUE_TEMPLATE_WITHOUT_DEFAULT_QUOTA(HttpStatus.CONFLICT, "Missing mandatory default quota for venue template"),
    VENUE_TEMPLATE_WITHOUT_DEFAULT_PRICE_TYPE(HttpStatus.CONFLICT, "Missing mandatory default price type for venue template"),
    VENUE_TEMPLATE_SECTOR_CODE_CONFLICT(HttpStatus.CONFLICT, "Venue Template Sector code already exists"),
    VENUE_TEMPLATE_SECTOR_CODE_REQUIRED(HttpStatus.BAD_REQUEST, "Venue Template Sector code is required"),
    VENUE_TEMPLATE_SECTOR_CODE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "Venue Template Sector code is not allowed"),
    VENUE_TEMPLATE_TAG_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue template tag group not found"),
    VENUE_TEMPLATE_TAG_GROUP_ALREADY_EXISTS(HttpStatus.CONFLICT, "Venue template tag group code already exists"),
    VENUE_TEMPLATE_TAG_GROUP_LIMIT_REACHED(HttpStatus.CONFLICT, "Venue template limit of 2 groups reached"),
    VENUE_TEMPLATE_TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue template tag not found for group"),
    VENUE_TEMPLATE_TAG_ALREADY_EXISTS(HttpStatus.CONFLICT, "Venue template tag code already exists for group"),
    VENUE_TEMPLATE_TAG_HAS_PRICE_TYPES(HttpStatus.FORBIDDEN, "Venue template tag with price types can't be deleted"),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue Template Seat not found"),
    VENUE_TEMPLATE_IN_USE(HttpStatus.PRECONDITION_FAILED, "Venue Template is already in use"),
    VENUE_TEMPLATE_VIEW_ROOT_MANDATORY(HttpStatus.PRECONDITION_FAILED, "Can't delete root Venue Template View because other views in the same venue templates remain"),
    VENUE_TEMPLATE_VIEW_BULK_BAD_REQUEST(HttpStatus.BAD_REQUEST, "The Venue Template View bulk request has views with repeated IDs, names or descriptions"),
    VENUE_TEMPLATE_VIEW_IS_LINKED(HttpStatus.PRECONDITION_FAILED, "Venue Template View has at least one link"),
    VENUE_TEMPLATE_VIEW_HAS_ORIGIN_VIEWS(HttpStatus.PRECONDITION_FAILED, "Venue Template View is referenced as an origin by at least another view"),
    VENUE_TEMPLATE_VIEW_LINK_CONFLICT(HttpStatus.CONFLICT, "Venue Template View cannot be linked to itself"),
    VENUE_TEMPLATE_VIEW_LINK_ALREADY_EXISTS(HttpStatus.CONFLICT, "Venue Template View Link already exists"),
    VENUE_TEMPLATE_VIEW_DESTINATION_CONFLICT(HttpStatus.CONFLICT, "Venue Template View can't have multiple origins"),
    VENUE_TEMPLATE_VIEW_LINK_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue Template View Link not found"),
    VENUE_TEMPLATE_VIEW_LINK_BULK_BAD_REQUEST(HttpStatus.BAD_REQUEST, "The Venue Template View link bulk request has links with repeated IDs or view IDs"),
    VENUE_TEMPLATE_VIEW_LINK_ROOT_CONFLICT(HttpStatus.CONFLICT, "Venue Template View root cannot be the destination of a link"),
    VENUE_TEMPLATE_ROW_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue Template Row not found"),
    VENUE_TEMPLATE_ROW_BULK_BAD_REQUEST(HttpStatus.BAD_REQUEST, "The Venue Template Row bulk request has rows with repeated IDs"),
    VENUE_TEMPLATE_SEATS_INVALID_ROW_IDS(HttpStatus.BAD_REQUEST, "Some seats have invalid row_id"),
    VENUE_TEMPLATE_VIEW_SVG_PARSE(HttpStatus.BAD_REQUEST, "Venue Template view svg could not be parsed"),
    VENUE_TEMPLATE_VIEW_SVG_REPEATED_ID(HttpStatus.BAD_REQUEST, "Venue Template view SVG contains repeated IDs"),
    VENUE_TEMPLATE_VIEW_SVG_MALFORMED_ID(HttpStatus.BAD_REQUEST, "Venue Template view SVG contains malformed IDs"),
    VENUE_TEMPLATE_VIEW_SVG_IDS_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue Template view SVG contains seat, not numbered zones or link IDs not found"),
    VENUE_TEMPLATE_VIEW_SVG_SERIALIZATION(HttpStatus.INTERNAL_SERVER_ERROR, "Venue Template view converted custom svg could not be serialized"),
    VENUE_TEMPLATE_NNZONE_MISSMATCH(HttpStatus.CONFLICT, "Not numbered zones missmatch with the venue template id"),
    VENUE_TEMPLATE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue Template image not found"),
    VENUE_TEMPLATE_IMAGE_BAD_ENCODING(HttpStatus.BAD_REQUEST, "Venue Template image has wrong base64 encoding"),
    VENUE_TEMPLATE_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "Venue template info not found"),
    VENUE_TEMPLATE_INFO_IMAGE_INVALID_POSITION(HttpStatus.BAD_REQUEST, "Venue template image must have a valid position"),
    VENUE_TEMPLATE_INFO_INVALID_NUM_OF_IMAGES(HttpStatus.BAD_REQUEST, "Venue template info has an invalid num of images"),
    VENUE_TEMPLATE_INFO_CONFLICT_TEMPLATE_ID(HttpStatus.CONFLICT, "Invalid template info type. Does not compatible with template type."),
    VENUE_TEMPLATE_INFO_CONFLICT_INVALID_IDS(HttpStatus.CONFLICT, "There are Ids not associated to template requested."),
    VENUE_TEMPLATE_INFO_3D_CONFIG_NO_CODES(HttpStatus.CONFLICT, "Enable 3D config should contains at least one code"),
    VENUE_TEMPLATE_INFO_CONFLICT_CREATION_SESSION_EXISTS(HttpStatus.CONFLICT, "A session template info was already created for the specified session id."),
    VENUE_TEMPLATE_INFO_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Session not found"),
    VENUE_TEMPLATE_INFO_SOURCE_AND_TARGET_TEMPLATES_CANNOT_HAVE_DIFFERENT_TYPES(HttpStatus.CONFLICT, "Source and target templates cannot have different types"),
    POSTAL_CODE_REQUIRED_FOR_TAX_CALCULATION(HttpStatus.BAD_REQUEST, "Postal code is required for this country's tax calculation"),
    VENUE_GOOGLE_PLACE_ID_REQUIRED(HttpStatus.BAD_REQUEST, "Venue must have googlePlaceId");


    private final HttpStatus httpStatus;
    private final String message;

    ApiMgmtVenueErrorCode(HttpStatus httpStatus, String message) {
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
