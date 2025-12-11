package es.onebox.mgmt.datasources.ms.venue;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.request.HttpRequestBuilder;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.IdNameListWithMetadata;
import es.onebox.mgmt.datasources.common.dto.CreateVenueTemplateRequest;
import es.onebox.mgmt.datasources.common.dto.PriceTypeCommunicationElementFilter;
import es.onebox.mgmt.datasources.common.dto.QuotaCapacity;
import es.onebox.mgmt.datasources.ms.venue.dto.PriceTypeCapacity;
import es.onebox.mgmt.datasources.ms.venue.dto.PriceTypeCommunicationElement;
import es.onebox.mgmt.datasources.ms.venue.dto.ProviderSector;
import es.onebox.mgmt.datasources.ms.venue.dto.Venue;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueItemPostRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.VenueItemPutRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.Venues;
import es.onebox.mgmt.datasources.ms.venue.dto.VenuesFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.space.VenueSpace;
import es.onebox.mgmt.datasources.ms.venue.dto.space.VenueSpaces;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReason;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReasonRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CloneVenueTemplateSector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateRow;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateTag;
import es.onebox.mgmt.datasources.ms.venue.dto.template.CreateVenueTemplateViewLink;
import es.onebox.mgmt.datasources.ms.venue.dto.template.DynamicTag;
import es.onebox.mgmt.datasources.ms.venue.dto.template.DynamicTagGroup;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Gate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.InteractiveVenue;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZone;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZoneCapacity;
import es.onebox.mgmt.datasources.ms.venue.dto.template.NotNumberedZoneFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.datasources.ms.venue.dto.template.RowDetail;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Sector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.TagWithGroup;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateInteractiveVenue;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateNotNumberedZone;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateRow;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateRowBulk;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateViewBulk;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateVipView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpsertVenueTemplateImage;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateBaseSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateImage;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateSeat;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateSector;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateViews;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateViewsFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplates;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatesFilter;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplatesFiltersRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoBulkUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoListResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionTemplateInfoStatusUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.SessionUpdateTemplateInfo;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoBaseResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoBulkUpdateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoCreateRequest;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoImagesDeleteFilterDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.TemplateInfoListResponse;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.UpdateTemplateInfoDefault;
import es.onebox.mgmt.datasources.ms.venue.dto.templateelements.enums.ElementInfoImageType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtVenueErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.pricetype.dto.PriceTypeTicketCommunicationElement;
import es.onebox.mgmt.venues.dto.CreateVenueTagConfigRequestDTO;
import es.onebox.mgmt.venues.dto.GateRequestDTO;
import es.onebox.mgmt.venues.dto.PriceTypeChannelContentFilterDTO;
import es.onebox.mgmt.venues.dto.PriceTypeRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagConfigRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTemplatesSeatExportFileField;
import es.onebox.mgmt.venues.dto.VenueTemplatesSectorExportFileField;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoBulkRequestDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementInfoSearchDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateSessionElementInfoSearchDTO;
import es.onebox.mgmt.venues.enums.ElementType;
import es.onebox.mgmt.venues.enums.VenueField;
import es.onebox.mgmt.venues.enums.VenueTemplateFilterField;
import es.onebox.mgmt.venues.enums.VenueTemplateSortField;
import es.onebox.servicepreview.core.context.ServicePreviewContext;
import es.onebox.tracer.okhttp.TracingInterceptor;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class MsVenueDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/venues-api/" + API_VERSION;

    private static final String VENUES = "/venues";
    private static final String VENUE = VENUES + "/{id}";
    private static final String SPACES = VENUE + "/spaces";
    private static final String SPACE_ID = SPACES + "/{spaceId}";
    private static final String VENUE_TEMPLATES = "/venue-templates";
    private static final String VENUE_TEMPLATE = VENUE_TEMPLATES + "/{id}";
    private static final String VENUE_TEMPLATE_VIEWS = VENUE_TEMPLATES + "/{id}/views";
    private static final String VENUE_TEMPLATE_ELEMENTS_INFO = VENUE_TEMPLATES + "/{templateId}/template-elements";
    private static final String VENUE_TEMPLATE_SESSIONS_ELEMENTS_INFO = VENUE_TEMPLATE_ELEMENTS_INFO + "/sessions/{sessionId}";
    private static final String VENUE_TEMPLATE_SESSIONS_ELEMENT_INFO = VENUE_TEMPLATE_ELEMENTS_INFO + "/{type}/{id}/sessions/{sessionId}";
    private static final String VENUE_TEMPLATE_SESSIONS_ELEMENT_INFO_STATUS = VENUE_TEMPLATE_SESSIONS_ELEMENT_INFO + "/status";
    private static final String VENUE_TEMPLATE_ELEMENT_INFO = VENUE_TEMPLATE_ELEMENTS_INFO + "/{type}/{id}";
    private static final String VENUE_TEMPLATE_ROWS = VENUE_TEMPLATES + "/{id}/rows";
    private static final String VENUE_TEMPLATE_SEATS = VENUE_TEMPLATES + "/{id}/seats";
    private static final String VENUE_TEMPLATE_VIEW = VENUE_TEMPLATES + "/{id}/views/{viewId}";
    private static final String VENUE_TEMPLATE_SECTOR = VENUE_TEMPLATE + "/sectors/{sectorId}";
    private static final String VENUE_TEMPLATE_ROW = VENUE_TEMPLATES + "/{id}/rows/{viewId}";
    private static final String VENUE_TEMPLATE_SEAT = VENUE_TEMPLATES + "/{id}/seats/{viewId}";
    private static final String VENUE_TEMPLATE_NOT_NUMBERED_ZONE = VENUE_TEMPLATE + "/notnumberedzones/{zoneId}";
    private static final String PRICE_TYPES = VENUE_TEMPLATE + "/priceTypes";
    private static final String PRICE_TYPE = PRICE_TYPES + "/{priceTypeId}";
    private static final String COMM_ELEMENTS = PRICE_TYPE + "/web-communication-elements";
    private static final String PRICE_TYPE_CAPACITY = VENUE_TEMPLATE + "/capacity/priceTypes";
    private static final String QUOTA_CAPACITY = VENUE_TEMPLATE + "/capacity/quotas";
    private static final String INTERACTIVE_VENUE = VENUE_TEMPLATE + "/interactive-venue";
    private static final String VENUE_TEMPLATE_REPORT = VENUE_TEMPLATE + "/exports";
    private static final String VENUE_TEMPLATE_DYNAMIC_TAG_GROUPS = VENUE_TEMPLATE + "/dynamic-tag-groups";
    private static final String VENUE_TEMPLATE_DYNAMIC_TAG_GROUP = VENUE_TEMPLATE_DYNAMIC_TAG_GROUPS + "/{tagGroupId}";
    private static final String VENUE_TEMPLATE_DYNAMIC_TAG_GROUP_TAGS = VENUE_TEMPLATE_DYNAMIC_TAG_GROUP + "/tags";
    private static final String SECTORS = "/sectors";
    private static final String SEATS = "/seats";
    private static final String VIEWS = "/views";
    private static final String VENUE_TEMPLATE_NOT_NUMBERED_ZONE_BULK_UPDATE = VENUE_TEMPLATE + "/notnumberedzones/bulk";
    private static final String VENUE_TEMPLATE_ID = "/{venueTemplateId}";
    private static final String PDF_TICKET_COMMUNICATION_ELEMENTS = VENUE_TEMPLATES + VENUE_TEMPLATE_ID + "/priceTypes/{priceTypeId}/pdf-communication-elements";
    private static final String PRINTER_TICKET_COMMUNICATION_ELEMENTS = VENUE_TEMPLATES + VENUE_TEMPLATE_ID + "/priceTypes/{priceTypeId}/printer-communication-elements";
    private static final String PASSBOOK_TICKET_COMMUNICATION_ELEMENTS = VENUE_TEMPLATES + VENUE_TEMPLATE_ID + "/priceTypes/{priceTypeId}/passbook-communication-elements";
    private static final String CHANGED_TICKET_COMMUNICATION_ELEMENTS = VENUE_TEMPLATES + VENUE_TEMPLATE_ID + "/ticket-contents/changed-price-types";

    private final HttpClient httpClient;

    private final CloseableHttpClient basicHttpClient;

    private final String baseUrl;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("NOT_FOUND", ApiMgmtErrorCode.NOT_FOUND);
        ERROR_CODES.put("403G0001", ApiMgmtErrorCode.VENUE_TEMPLATE_CREATION_FORBIDDEN);
        ERROR_CODES.put("MV0001", ApiMgmtErrorCode.EVENT_NOT_FOUND);
        ERROR_CODES.put("400G0001", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("500C0005", ApiMgmtErrorCode.PERSISTENCE_ERROR);
        ERROR_CODES.put("VENUE_TEMPLATE_NOT_FOUND", ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_INVALID_TYPE", ApiMgmtErrorCode.VENUE_TEMPLATE_INVALID_TYPE);
        ERROR_CODES.put("VENUE_TEMPLATE_HAS_SESSIONS", ApiMgmtErrorCode.VENUE_TEMPLATE_HAS_SESSION);
        ERROR_CODES.put("VENUE_TEMPLATE_SESSIONS_DELETE", ApiMgmtErrorCode.VENUE_TEMPLATE_SESSIONS_DELETE);
        ERROR_CODES.put("VENUE_TEMPLATE_AVET_DELETE", ApiMgmtErrorCode.VENUE_TEMPLATE_AVET_DELETE);
        ERROR_CODES.put("VENUE_TEMPLATE_AVET_NOT_ALLOWED", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_AVET_NOT_ALLOWED);
        ERROR_CODES.put("VENUE_TEMPLATE_TAG_DEFAULT_DELETE", ApiMgmtErrorCode.VENUE_TEMPLATE_TAG_DEFAULT_DELETE);
        ERROR_CODES.put("VENUE_TEMPLATE_TAG_HAS_SALES", ApiMgmtErrorCode.VENUE_TEMPLATE_TAG_SALES_DELETE);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_NOT_FOUND", ApiMgmtErrorCode.VENUE_TEMPLATE_VIEW_NOT_FOUND);
        ERROR_CODES.put("INVALID_HEX_COLOR", ApiMgmtErrorCode.INVALID_HEX_COLOR);
        ERROR_CODES.put("INVALID_FILENAME_NO_EXTENSION", ApiMgmtErrorCode.INVALID_FILENAME_NO_EXTENSION);
        ERROR_CODES.put("INVALID_FILENAME_HAS_SEPARATOR", ApiMgmtErrorCode.INVALID_FILENAME_HAS_SEPARATOR);
        ERROR_CODES.put("VENUE_TEMPLATE_NAME_CONFLICT", ApiMgmtErrorCode.NAME_CONFLICT);
        ERROR_CODES.put("VENUE_TEMPLATE_INVALID_TAGS", ApiMgmtErrorCode.VENUE_TEMPLATE_INVALID_TAGS);
        ERROR_CODES.put("VENUE_TEMPLATE_UPDATING_TAGS", ApiMgmtErrorCode.PERSISTENCE_ERROR);
        ERROR_CODES.put("VENUE_TEMPLATE_TAG_PRICE_TYPE_UPDATE_AVET_NOT_ALLOWED",
                ApiMgmtErrorCode.VENUE_TEMPLATE_TAG_PRICE_TYPE_UPDATE_AVET_NOT_ALLOWED);
        ERROR_CODES.put("VENUE_TEMPLATE_UPDATING_STATUS", ApiMgmtErrorCode.VENUE_TEMPLATE_INVALID_STATUS);
        ERROR_CODES.put("VENUE_TEMPLATE_SECTOR_CODE_CONFLICT", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SECTOR_CODE_CONFLICT);
        ERROR_CODES.put("VENUE_TEMPLATE_SECTOR_DEFAULT_DELETE", ApiMgmtErrorCode.VENUE_TEMPLATE_SECTOR_DEFAULT_DELETE);
        ERROR_CODES.put("VENUE_TEMPLATE_SECTOR_NOT_FOUND", ApiMgmtErrorCode.VENUE_TEMPLATE_SECTOR_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_NNZONE_HAS_SESSIONS", ApiMgmtErrorCode.VENUE_TEMPLATE_NNZONE_HAS_SESSION);
        ERROR_CODES.put("VENUE_TEMPLATE_SECTOR_HAS_SESSIONS", ApiMgmtErrorCode.VENUE_TEMPLATE_SECTOR_HAS_SESSIONS);
        ERROR_CODES.put("VENUE_TEMPLATE_NNZONE_NOT_FOUND", ApiMgmtErrorCode.VENUE_TEMPLATE_NNZONE_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_NNZONE_MISSMATCH", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_NNZONE_MISSMATCH);
        ERROR_CODES.put("VENUE_TEMPLATE_ITEM_NOT_MATCH", ApiMgmtErrorCode.VENUE_TEMPLATE_ITEM_NOT_MATCH);
        ERROR_CODES.put("VENUE_TEMPLATE_INVALID_SPACE", ApiMgmtErrorCode.INVALID_VENUE_SPACE);
        ERROR_CODES.put("VENUE_TEMPLATE_SPACE_NOT_FOUND", ApiMgmtErrorCode.INVALID_VENUE_SPACE);
        ERROR_CODES.put("VENUE_TEMPLATE_NOT_APPLICABLE_TO_GRAPHIC", ApiMgmtErrorCode.VENUE_TEMPLATE_NOT_APPLICABLE_TO_GRAPHIC);
        ERROR_CODES.put("VENUE_TEMPLATE_AVET_INITIALIZATION_ERROR", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_AVET_INITIALIZATION_ERROR);
        ERROR_CODES.put("EXPORT_STATUS_NOT_FOUND", ApiMgmtErrorCode.EXPORT_STATUS_NOT_FOUND);
        ERROR_CODES.put("EXPORT_UPLOAD_EXCEPTION", ApiMgmtErrorCode.EXPORT_UPLOAD_EXCEPTION);
        ERROR_CODES.put("INVALID_EXPORT_TYPE", ApiMgmtErrorCode.INVALID_EXPORT_TYPE);
        ERROR_CODES.put("INVALID_VENUE_TEMPLATE_TYPE_FOR_EXPORT", ApiMgmtErrorCode.INVALID_VENUE_TEMPLATE_TYPE_FOR_EXPORT);
        ERROR_CODES.put("INVALID_TAG_ID", ApiMgmtVenueErrorCode.INVALID_TAG_ID);
        ERROR_CODES.put("DEFAULT_GATE_RESTRICTION", ApiMgmtVenueErrorCode.DEFAULT_GATE_RESTRICTION);
        ERROR_CODES.put("VENUE_TEMPLATE_CAPACITY_NOT_MODIFIABLE", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_CAPACITY_NOT_MODIFIABLE);
        ERROR_CODES.put("VENUE_TEMPLATE_CAPACITY_INVALID_PARAM", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_CAPACITY_INVALID_PARAM);
        ERROR_CODES.put("VENUE_TEMPLATE_NNZONE_UNSUPPORTED_OPERATION", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_CAPACITY_UNSUPPORTED_OPERATION);
        ERROR_CODES.put("GATE_NAME_ALREADY_IN_USE", ApiMgmtVenueErrorCode.GATE_NAME_ALREADY_IN_USE);
        ERROR_CODES.put("GATE_CODE_ALREADY_IN_USE", ApiMgmtVenueErrorCode.GATE_CODE_ALREADY_IN_USE);
        ERROR_CODES.put("PRICE_ZONE_NAME_ALREADY_IN_USE", ApiMgmtVenueErrorCode.PRICE_ZONE_NAME_ALREADY_IN_USE);
        ERROR_CODES.put("PRICE_ZONE_CODE_ALREADY_IN_USE", ApiMgmtVenueErrorCode.PRICE_ZONE_CODE_ALREADY_IN_USE);
        ERROR_CODES.put("INVALID_COMM_ELEM_VALUE", ApiMgmtVenueErrorCode.INVALID_COMM_ELEM_VALUE);
        ERROR_CODES.put("INVALID_COMM_ELEM_LANG", ApiMgmtVenueErrorCode.INVALID_COMM_ELEM_LANG);
        ERROR_CODES.put("INVALID_COMM_ELEM_TYPE", ApiMgmtVenueErrorCode.INVALID_COMM_ELEM_TYPE);
        ERROR_CODES.put("PRICE_ZONE_COMMUNICATION_ELEMENT_NOT_DELETED", ApiMgmtVenueErrorCode.PRICE_ZONE_COMMUNICATION_ELEMENT_NOT_DELETED);
        ERROR_CODES.put("PRICE_ZONE_COMMUNICATION_ELEMENT_NOT_UPDATED", ApiMgmtVenueErrorCode.PRICE_ZONE_COMMUNICATION_ELEMENT_NOT_UPDATED);
        ERROR_CODES.put("PRICE_TYPE_TRANSLATION_MANDATORY", ApiMgmtVenueErrorCode.PRICE_TYPE_TRANSLATION_MANDATORY);
        ERROR_CODES.put("PRICE_ZONE_COMMUNICATION_ELEMENT_NOT_FOUND", ApiMgmtVenueErrorCode.PRICE_ZONE_COMMUNICATION_ELEMENT_NOT_FOUND);
        ERROR_CODES.put("ENTITY_IS_NOT_VENUE_OWNER", ApiMgmtVenueErrorCode.ENTITY_IS_NOT_VENUE_OWNER);
        ERROR_CODES.put("UNDELETABLE_REFERENCED_VENUE", ApiMgmtVenueErrorCode.UNDELETABLE_REFERENCED_VENUE);
        ERROR_CODES.put("VENUE_NAME_IN_USE", ApiMgmtVenueErrorCode.VENUE_NAME_IN_USE);
        ERROR_CODES.put("ADDRESS_INFO_REQUIRED", ApiMgmtVenueErrorCode.ADDRESS_INFO_REQUIRED);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_NAME_CONFLICT", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_NAME_CONFLICT);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_DESCRIPTION_CONFLICT", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_CODE_CONFLICT);
        ERROR_CODES.put("INVALID_PARAM", ApiMgmtVenueErrorCode.INVALID_PARAM);
        ERROR_CODES.put("CALENDAR_NOT_FOUND", ApiMgmtVenueErrorCode.CALENDAR_NOT_FOUND);
        ERROR_CODES.put("UNACCESSIBLE_CALENDAR", ApiMgmtVenueErrorCode.UNACCESSIBLE_CALENDAR);
        ERROR_CODES.put("SPACE_NAME_IN_USE", ApiMgmtVenueErrorCode.SPACE_NAME_IN_USE);
        ERROR_CODES.put("UNDELETABLE_DEFAULT_VENUE_SPACE", ApiMgmtVenueErrorCode.UNDELETABLE_DEFAULT_VENUE_SPACE);
        ERROR_CODES.put("LATITUDE_COORDINATE_OUT_OF_RANGE", ApiMgmtVenueErrorCode.LATITUDE_COORDINATE_OUT_OF_RANGE);
        ERROR_CODES.put("LONGITUDE_COORDINATE_OUT_OF_RANGE", ApiMgmtVenueErrorCode.LONGITUDE_COORDINATE_OUT_OF_RANGE);
        ERROR_CODES.put("VENUE_WITHOUT_DEFAULT_SPACE_NOT_ALLOWED", ApiMgmtVenueErrorCode.VENUE_WITHOUT_DEFAULT_SPACE_NOT_ALLOWED);
        ERROR_CODES.put("ENTITY_IS_NOT_AVET", ApiMgmtVenueErrorCode.ENTITY_IS_NOT_AVET);
        ERROR_CODES.put("AMQP_PUSH_EXCEPTION", ApiMgmtErrorCode.AMQP_PUSH_EXCEPTION);
        ERROR_CODES.put("VENUE_TEMPLATE_PRICE_TYPE_NOT_FOUND", ApiMgmtErrorCode.VENUE_TEMPLATE_PRICE_TYPE_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_WITHOUT_DEFAULT_TAG", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_WITHOUT_DEFAULT_TAG);
        ERROR_CODES.put("VENUE_TEMPLATE_WITHOUT_DEFAULT_QUOTA", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_WITHOUT_DEFAULT_QUOTA);
        ERROR_CODES.put("VENUE_TEMPLATE_WITHOUT_DEFAULT_PRICE_TYPE", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_WITHOUT_DEFAULT_PRICE_TYPE);
        ERROR_CODES.put("VENUE_TEMPLATE_IN_USE", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_IN_USE);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_ROOT_MANDATORY", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_ROOT_MANDATORY);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_BULK_BAD_REQUEST", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_BULK_BAD_REQUEST);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_IS_LINKED", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_IS_LINKED);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_HAS_ORIGIN_VIEWS", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_HAS_ORIGIN_VIEWS);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_LINK_CONFLICT", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_LINK_CONFLICT);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_LINK_ALREADY_EXISTS", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_LINK_ALREADY_EXISTS);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_DESTINATION_CONFLICT", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_DESTINATION_CONFLICT);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_LINK_NOT_FOUND", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_LINK_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_LINK_BULK_BAD_REQUEST", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_LINK_BULK_BAD_REQUEST);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_LINK_ROOT_CONFLICT", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_LINK_ROOT_CONFLICT);
        ERROR_CODES.put("VENUE_TEMPLATE_ROW_NOT_FOUND", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_ROW_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_ROW_BULK_BAD_REQUEST", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_ROW_BULK_BAD_REQUEST);
        ERROR_CODES.put("VENUE_TEMPLATE_SEATS_INVALID_ROW_IDS", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SEATS_INVALID_ROW_IDS);
        ERROR_CODES.put("VENUE_TEMPLATE_TAG_GROUP_NOT_FOUND", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_TAG_GROUP_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_TAG_GROUP_ALREADY_EXISTS", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_TAG_GROUP_ALREADY_EXISTS);
        ERROR_CODES.put("VENUE_TEMPLATE_TAG_GROUP_LIMIT_REACHED", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_TAG_GROUP_LIMIT_REACHED);
        ERROR_CODES.put("VENUE_TEMPLATE_TAG_NOT_FOUND", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_TAG_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_TAG_ALREADY_EXISTS", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_TAG_ALREADY_EXISTS);
        ERROR_CODES.put("VENUE_TEMPLATE_ID_CONFLICT", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_ID_CONFLICT);
        ERROR_CODES.put("VENUE_TEMPLATE_TAG_HAS_PRICE_TYPES", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_TAG_HAS_PRICE_TYPES);
        ERROR_CODES.put("SEAT_NOT_FOUND", ApiMgmtVenueErrorCode.SEAT_NOT_FOUND);
        ERROR_CODES.put("BAD_PARAMETER", CoreErrorCode.BAD_PARAMETER);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_SVG_PARSE", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_SVG_PARSE);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_SVG_REPEATED_ID", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_SVG_REPEATED_ID);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_SVG_MALFORMED_ID", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_SVG_MALFORMED_ID);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_SVG_IDS_NOT_FOUND", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_SVG_IDS_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_VIEW_SVG_SERIALIZATION", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_VIEW_SVG_SERIALIZATION);
        ERROR_CODES.put("VENUE_TEMPLATE_IMAGE_NOT_FOUND", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_IMAGE_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_IMAGE_BAD_ENCODING", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_IMAGE_BAD_ENCODING);
        ERROR_CODES.put("VENUE_TEMPLATE_INFO_NOT_FOUND", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INFO_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_INFO_IMAGE_INVALID_POSITION", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INFO_IMAGE_INVALID_POSITION);
        ERROR_CODES.put("VENUE_TEMPLATE_INFO_INVALID_NUM_OF_IMAGES", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INFO_INVALID_NUM_OF_IMAGES);
        ERROR_CODES.put("VENUE_TEMPLATE_INFO_CONFLICT_TEMPLATE_ID", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INFO_CONFLICT_TEMPLATE_ID);
        ERROR_CODES.put("VENUE_TEMPLATE_INFO_CONFLICT_INVALID_IDS", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INFO_CONFLICT_INVALID_IDS);
        ERROR_CODES.put("VENUE_TEMPLATE_INFO_3D_CONFIG_NO_CODES", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INFO_3D_CONFIG_NO_CODES);
        ERROR_CODES.put("BAD_REQUEST_PARAMETER", ApiMgmtVenueErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("VENUE_TEMPLATE_INFO_CONFLICT_CREATION_SESSION_EXISTS", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INFO_CONFLICT_CREATION_SESSION_EXISTS);
        ERROR_CODES.put("VENUE_TEMPLATE_INFO_SESSION_NOT_FOUND", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INFO_SESSION_NOT_FOUND);
        ERROR_CODES.put("VENUE_TEMPLATE_INFO_SOURCE_AND_TARGET_TEMPLATES_CANNOT_HAVE_DIFFERENT_TYPES", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_INFO_SOURCE_AND_TARGET_TEMPLATES_CANNOT_HAVE_DIFFERENT_TYPES);
        ERROR_CODES.put("POSTAL_CODE_REQUIRED_FOR_TAX_CALCULATION", ApiMgmtVenueErrorCode.POSTAL_CODE_REQUIRED_FOR_TAX_CALCULATION);
        ERROR_CODES.put("VENUE_GOOGLE_PLACE_ID_REQUIRED", ApiMgmtVenueErrorCode.VENUE_GOOGLE_PLACE_ID_REQUIRED);
        ERROR_CODES.put("VENUE_TEMPLATE_SECTOR_CODE_NOT_ALLOWED", ApiMgmtVenueErrorCode.VENUE_TEMPLATE_SECTOR_CODE_NOT_ALLOWED);
    }

    @Autowired
    public MsVenueDatasource(@Value("${clients.services.ms-venue}") String baseUrl,
                             ObjectMapper jacksonMapper,
                             TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(15000L)
                .build();

        this.basicHttpClient = HttpClients.createDefault();
        this.baseUrl = baseUrl + BASE_PATH;
    }

    public Venue getVenue(Long venueId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE)
                .pathParams(venueId)
                .execute(Venue.class);
    }

    public Venues getVenues(long operatorId, VenuesFilter filter, SortOperator<String> sort, List<String> fields) {
        QueryParameters.Builder params = new QueryParameters.Builder()
                .addQueryParameters(filter);
        params.addQueryParameter("operatorId", String.valueOf(operatorId));
        ConverterUtils.checkSortFields(sort, params, VenueField::byName);
        ConverterUtils.checkFilterFields(fields, params, VenueField::byName);

        return httpClient.buildRequest(HttpMethod.GET, VENUES)
                .params(params.build())
                .execute(Venues.class);
    }

    public VenueTemplate getVenueTemplate(Long id) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE)
                .pathParams(id)
                .execute(VenueTemplate.class);
    }

    public VenueTemplates getVenueTemplates(Long operatorId, VenueTemplatesFilter filter,
                                            SortOperator<String> sort, List<String> fields) {
        QueryParameters.Builder params = new QueryParameters.Builder()
                .addQueryParameters(filter);
        params.addQueryParameter("operatorId", String.valueOf(operatorId));
        ConverterUtils.checkSortFields(sort, params, VenueTemplateSortField::byName);
        ConverterUtils.checkFilterFields(fields, params, VenueTemplateFilterField::byName);

        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATES)
                .params(params.build())
                .execute(VenueTemplates.class);
    }

    public Long createVenueTemplate(CreateVenueTemplateRequest venueTemplateRequest) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATES)
                .pathParams(venueTemplateRequest.getEventId())
                .body(new ClientRequestBody(venueTemplateRequest))
                .execute(IdDTO.class).getId();
    }

    public void updateVenueTemplate(Long venueTemplateId, UpdateVenueTemplate updateVenueTemplate) {

        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(updateVenueTemplate))
                .execute();
    }

    public void updateAssignTags(Long venueTemplateId, UpdateVenueTemplateSeat[] requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE + "/seats")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(requestDTO))
                .execute();
    }

    public void updateNotNumberedZoneTags(Long venueTemplateId, VenueTagDTO[] notNumberedZone) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE + "/notnumberedzones")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(notNumberedZone))
                .execute();
    }

    public InputStream getVenueTemplateMap(Long id) {

        String url = baseUrl + VENUE_TEMPLATES + "/" + id + "/map";
        try {
            ClassicRequestBuilder requestBuilder = ClassicRequestBuilder.get()
                    .setUri(url)
                    .setHeader(HttpHeaders.ACCEPT, "application/x-protobuf");
            if (ServicePreviewContext.getHeader(ServicePreviewContext.HEADER_FEATURE) != null) {
                requestBuilder = requestBuilder.setHeader(ServicePreviewContext.HEADER_FEATURE, ServicePreviewContext.getHeader(ServicePreviewContext.HEADER_FEATURE));
            }
            CloseableHttpResponse response = basicHttpClient.execute(requestBuilder.build());
            if (response.getCode() == HttpStatus.SC_OK) {
                return response.getEntity().getContent();
            } else {
                throw new OneboxRestException(ApiMgmtErrorCode.GENERIC_ERROR,
                        "Error querying capacity map. HTTP status "
                                + response.getCode()
                                + ", " + response.getReasonPhrase(), null);
            }
        } catch (IOException e) {
            throw new OneboxRestException(ApiMgmtErrorCode.GENERIC_ERROR, "Request problem venueTemplate with id: " + id, e);
        }
    }

    public Long createVenueTemplateView(Long venueTemplateId, UpdateVenueTemplateView body) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_VIEWS)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(body))
                .execute(IdDTO.class).getId();
    }

    public VenueTemplateViews getVenueTemplateViews(Long venueTemplateId, VenueTemplateViewsFilter filter) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(filter).build();
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_VIEWS)
                .pathParams(venueTemplateId)
                .params(params)
                .execute(VenueTemplateViews.class);
    }

    public void updateVenueTemplateVipViews(Long venueTemplateId, List<UpdateVenueTemplateVipView> body) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_VIEWS + "/vip").pathParams(venueTemplateId)
                .body(new ClientRequestBody(body)).execute();
    }

    public void updateVenueTemplateViews(Long venueTemplateId, List<UpdateVenueTemplateViewBulk> viewList) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_VIEWS)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(viewList)).execute();
    }

    public void updateVenueTemplateView(Long venueTemplateId, Long viewId, UpdateVenueTemplateView body) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_VIEW)
                .pathParams(venueTemplateId, viewId)
                .body(new ClientRequestBody(body)).execute();
    }

    public VenueTemplateView getVenueTemplateView(Long venueTemplateId, String viewId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_VIEW)
                .pathParams(venueTemplateId, viewId)
                .execute(VenueTemplateView.class);
    }

    public void deleteVenueTemplateView(Long venueTemplateId, Long viewId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_VIEW)
                .pathParams(venueTemplateId, viewId)
                .execute();
    }

    public Long createVenueTemplateViewLink(Long venueTemplateId, Integer viewId, CreateVenueTemplateViewLink body) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_VIEW + "/links")
                .pathParams(venueTemplateId, viewId)
                .body(new ClientRequestBody(body))
                .execute(IdDTO.class).getId();
    }

    public void deleteVenueTemplateViewLink(Long venueTemplateId, Long viewId, Long linkId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_VIEW + "/links/{linkId}")
                .pathParams(venueTemplateId, viewId, linkId)
                .execute();
    }

    public void updateVenueTemplateViewTemplate(Long venueTemplateId, Long viewId, String template) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_VIEW + "/template")
                .pathParams(venueTemplateId, viewId)
                .headers(new RequestHeaders.Builder()
                        .addHeader("Content-Type", "text/plain")
                        .build())
                .body(new ClientRequestBody(template) {
                    @Override
                    public RequestBody buildRequestBody(ObjectMapper mapper) {
                        return RequestBody.create(template, MediaType.parse("text/plain; charset=utf-8"));
                    }
                })
                .execute();
    }

    public List<VenueTemplateImage> getVenueTemplateImages(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/images")
                .pathParams(venueTemplateId)
                .execute(ListType.of(VenueTemplateImage.class));
    }

    public VenueTemplateImage upsertVenueTemplateImage(Long venueTemplateId, UpsertVenueTemplateImage body) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE + "/images")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(body))
                .execute(VenueTemplateImage.class);
    }

    public void deleteVenueTemplateImage(Long venueTemplateId, Long imageId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE + "/images/{imageId}")
                .pathParams(venueTemplateId, imageId)
                .execute();
    }

    public RowDetail getVenueTemplateRow(Long venueTemplateId, Integer rowId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_ROW)
                .pathParams(venueTemplateId, rowId)
                .execute(RowDetail.class);
    }

    public Long createVenueTemplateRow(Long venueTemplateId, CreateVenueTemplateRow body) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_ROWS)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(body))
                .execute(IdDTO.class).getId();
    }

    public List<IdDTO> createVenueTemplateRows(Long venueTemplateId, List<CreateVenueTemplateRow> body) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_ROWS + "/bulk")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(body))
                .execute(ListType.of(IdDTO.class));
    }

    public void updateVenueTemplateRow(Long venueTemplateId, Long rowId, UpdateVenueTemplateRow body) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_ROW)
                .pathParams(venueTemplateId, rowId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void updateVenueTemplateRows(Long venueTemplateId, List<UpdateVenueTemplateRowBulk> body) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_ROWS)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteVenueTemplateRow(Long venueTemplateId, Long rowId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_ROW)
                .pathParams(venueTemplateId, rowId)
                .execute();
    }

    public VenueTemplateSeat getVenueTemplateSeat(Long venueTemplateId, Integer seatId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_SEAT)
                .pathParams(venueTemplateId, seatId)
                .execute(VenueTemplateSeat.class);
    }

    public List<VenueTemplateBaseSeat> getVenueTemplateSeatsByRows(Long venueTemplateId, List<Integer> rowIds) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        rowIds.forEach(id -> params.addQueryParameter("ids", id));

        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_ROWS)
                .pathParams(venueTemplateId)
                .params(params.build())
                .execute(ListType.of(VenueTemplateBaseSeat.class));
    }

    public List<IdDTO> createVenueTemplateSeats(Long venueTemplateId, List<CreateVenueTemplateSeat> requestDTO) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_SEATS)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(requestDTO))
                .execute(ListType.of(IdDTO.class));
    }

    public void deleteVenueTemplateSeat(Long venueTemplateId, List<Integer> seatIds) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        seatIds.forEach(id -> params.addQueryParameter("seatIds", id));
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_SEATS)
                .pathParams(venueTemplateId)
                .params(params.build())
                .execute();
    }

    public List<BlockingReason> getBlockingReasons(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/blockingReasons")
                .pathParams(venueTemplateId)
                .execute(ListType.of(BlockingReason.class));
    }

    public Long createBlockingReason(Long venueTemplateId, BlockingReasonRequest requestDTO) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE + "/blockingReasons")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(requestDTO))
                .execute(IdDTO.class).getId();
    }

    public void updateBlockingReason(Long venueTemplateId, Long blockingReasonId, BlockingReasonRequest requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE + "/blockingReasons/{blockingReasonId}")
                .pathParams(venueTemplateId, blockingReasonId)
                .body(new ClientRequestBody(requestDTO))
                .execute();
    }

    public void deleteBlockingReason(Long venueTemplateId, Long blockingReasonId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE + "/blockingReasons/{blockingReasonId}")
                .pathParams(venueTemplateId, blockingReasonId)
                .execute();
    }

    public List<PriceType> getPriceTypes(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/priceTypes")
                .pathParams(venueTemplateId)
                .execute(ListType.of(PriceType.class));
    }

    public Long createPriceType(Long venueTemplateId, PriceTypeRequestDTO requestDTO) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE + "/priceTypes")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(buildTagRequestBody(requestDTO)))
                .execute(IdDTO.class).getId();
    }

    public void updatePriceType(Long venueTemplateId, Long priceTypeId, PriceTypeRequestDTO requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE + "/priceTypes/{priceTypeId}")
                .pathParams(venueTemplateId, priceTypeId)
                .body(new ClientRequestBody(buildTagRequestBody(requestDTO)))
                .execute();
    }

    public void deletePriceType(Long venueTemplateId, Long priceTypeId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE + "/priceTypes/{priceTypeId}")
                .pathParams(venueTemplateId, priceTypeId)
                .execute();
    }

    public List<Quota> getQuotas(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/quotas")
                .pathParams(venueTemplateId)
                .execute(ListType.of(Quota.class));
    }

    public Long createQuota(Long venueTemplateId, CreateVenueTagConfigRequestDTO requestDTO) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE + "/quotas")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(buildTagRequestBody(requestDTO)))
                .execute(IdDTO.class).getId();
    }

    public void updateQuota(Long venueTemplateId, Long quotaId, VenueTagConfigRequestDTO requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE + "/quotas/{quotaId}")
                .pathParams(venueTemplateId, quotaId)
                .body(new ClientRequestBody(buildTagRequestBody(requestDTO)))
                .execute();
    }

    public void deleteQuota(Long venueTemplateId, Long quotaId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE + "/quotas/{quotaId}")
                .pathParams(venueTemplateId, quotaId)
                .execute();
    }

    public List<TagWithGroup> getTags(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/tags")
                .pathParams(venueTemplateId)
                .execute(ListType.of(TagWithGroup.class));
    }

    public Long createGate(Long venueTemplateId, GateRequestDTO requestDTO) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE + "/gates")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(buildTagRequestBody(requestDTO)))
                .execute(IdDTO.class).getId();
    }

    public void updateGate(Long venueTemplateId, Long gateId, GateRequestDTO requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE + "/gates/{gateId}")
                .pathParams(venueTemplateId, gateId)
                .body(new ClientRequestBody(buildTagRequestBody(requestDTO)))
                .execute();
    }

    public void deleteGate(Long venueTemplateId, Long gateId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE + "/gates/{gateId}")
                .pathParams(venueTemplateId, gateId)
                .execute();
    }


    public List<Gate> getGates(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/gates")
                .pathParams(venueTemplateId)
                .execute(ListType.of(Gate.class));
    }

    public List<Sector> getSectors(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/sectors")
                .pathParams(venueTemplateId)
                .execute(ListType.of(Sector.class));
    }

    public Sector getSector(Long venueTemplateId, Long sectorId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/sectors/{sectorId}")
                .pathParams(venueTemplateId, sectorId)
                .execute(Sector.class);
    }

    public ProviderSector getProviderSector(String provider, String sectorCode) {
        return httpClient.buildRequest(HttpMethod.GET, "/providers/{provider}/sectors/{sectorCode}")
                .pathParams(provider, sectorCode)
                .execute(ProviderSector.class);
    }

    public Long createSector(Long venueTemplateId, VenueTemplateSector requestDTO) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE + "/sectors")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(requestDTO))
                .execute(IdDTO.class).getId();
    }

    public Long cloneSector(Long venueTemplateId, Long sectorId, CloneVenueTemplateSector cloneSectorDTO) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_SECTOR + "/clone")
                .pathParams(venueTemplateId, sectorId)
                .body(new ClientRequestBody(cloneSectorDTO))
                .execute(IdDTO.class).getId();
    }

    public void updateSector(Long venueTemplateId, Long sectorId, VenueTemplateSector sectorDto) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE + "/sectors/{sectorId}")
                .pathParams(venueTemplateId, sectorId)
                .body(new ClientRequestBody(sectorDto))
                .execute();
    }

    public void deleteSector(Long venueTemplateId, Long sectorId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE + "/sectors/{sectorId}")
                .pathParams(venueTemplateId, sectorId)
                .execute();
    }

    public NotNumberedZoneCapacity getNotNumberedZone(Long venueTemplateId, Long zoneId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/notnumberedzones/{zoneId}")
                .pathParams(venueTemplateId, zoneId)
                .execute(NotNumberedZoneCapacity.class);
    }

    public List<NotNumberedZone> getNotNumberedZones(Long venueTemplateId, NotNumberedZoneFilter filter) {
        QueryParameters params;
        HttpRequestBuilder request = httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/notnumberedzones")
                .pathParams(venueTemplateId);
        if (filter != null && CollectionUtils.isNotEmpty(filter.getId())) {
            params = new QueryParameters.Builder().addQueryParameters(filter).build();
            request.params(params);
        }
        return request.execute(ListType.of(NotNumberedZone.class));
    }

    public List<NotNumberedZoneCapacity> getNotNumberedZones(Long venueTemplateId, Long sectorId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE + "/sectors/{sectorId}/notnumberedzones")
                .pathParams(venueTemplateId, sectorId)
                .execute(ListType.of(NotNumberedZoneCapacity.class));
    }

    public IdDTO createNotNumberedZone(Long venueTemplateId, NotNumberedZone body) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE + "/notnumberedzones")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(body))
                .execute(IdDTO.class);
    }

    public List<IdDTO> createNotNumberedZones(Long venueTemplateId, Set<NotNumberedZone> nnZone) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE + "/notnumberedzones/bulk")
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(nnZone))
                .execute(ListType.of(IdDTO.class));
    }

    public Long cloneNotNumberedZone(Long venueTemplateId, Long nnZoneId, NotNumberedZone body) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_NOT_NUMBERED_ZONE + "/clone")
                .pathParams(venueTemplateId, nnZoneId).body(new ClientRequestBody(body))
                .execute(IdDTO.class).getId();
    }

    public void updateNotNumberedZone(Long venueTemplateId, Long notNumberedZoneId, UpdateNotNumberedZone requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_NOT_NUMBERED_ZONE)
                .pathParams(venueTemplateId, notNumberedZoneId)
                .body(new ClientRequestBody(requestDTO))
                .execute();
    }

    public void updateNotNumberedZoneBulk(Long venueTemplateId, Set<UpdateNotNumberedZone> body) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_NOT_NUMBERED_ZONE_BULK_UPDATE)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public void deleteNotNumberedZone(Long venueTemplateId, Long notNumberedZoneId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_NOT_NUMBERED_ZONE)
                .pathParams(venueTemplateId, notNumberedZoneId)
                .execute();
    }

    private CreateVenueTemplateTag buildTagRequestBody(VenueTagConfigRequestDTO requestDTO) {
        return new CreateVenueTemplateTag(requestDTO.getName(), requestDTO.getCode(), requestDTO.getColor(),
                null, null, null);
    }

    private CreateVenueTemplateTag buildTagRequestBody(CreateVenueTagConfigRequestDTO requestDTO) {
        return new CreateVenueTemplateTag(requestDTO.getName(), requestDTO.getCode(), requestDTO.getColor(),
                null, null, null);
    }

    private CreateVenueTemplateTag buildTagRequestBody(PriceTypeRequestDTO requestDTO) {
        return new CreateVenueTemplateTag(requestDTO.getName(), requestDTO.getCode(), requestDTO.getColor(),
                requestDTO.getPriority(), null, requestDTO.getPriceTypeAdditionalConfigDTO());
    }

    private CreateVenueTemplateTag buildTagRequestBody(GateRequestDTO requestDTO) {
        return new CreateVenueTemplateTag(requestDTO.getName(), requestDTO.getCode(), requestDTO.getColor(),
                null, requestDTO.getDefault(), null);
    }

    public IdNameListWithMetadata getVenueTemplatesFilterOptions(String filterName, VenueTemplatesFiltersRequest request) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATES + "/filters/{filter}")
                .pathParams(filterName)
                .params(params)
                .execute(IdNameListWithMetadata.class);
    }

    public List<PriceTypeCommunicationElement> getPriceTypeCommElements(Long venueTemplateId, Long priceTypeId, PriceTypeChannelContentFilterDTO filter) {
        QueryParameters params = new QueryParameters.Builder().addQueryParameters(filter).build();
        return httpClient.buildRequest(HttpMethod.GET, COMM_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .params(params)
                .execute(ListType.of(PriceTypeCommunicationElement.class));
    }

    public void upsertPriceTypeCommElements(Long venueTemplateId, Long priceTypeId,
                                            List<PriceTypeCommunicationElement> commElements) {
        httpClient.buildRequest(HttpMethod.POST, COMM_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .body(new ClientRequestBody(commElements))
                .execute();
    }

    public List<PriceTypeCapacity> getPriceTypeCapacity(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, PRICE_TYPE_CAPACITY)
                .pathParams(venueTemplateId)
                .execute(ListType.of(PriceTypeCapacity.class));
    }

    public List<QuotaCapacity> getQuotaCapacity(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, QUOTA_CAPACITY)
                .pathParams(venueTemplateId)
                .execute(ListType.of(QuotaCapacity.class));
    }

    public void updateQuotaCapacity(Long venueTemplateId, List<QuotaCapacity> requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, QUOTA_CAPACITY)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(requestDTO))
                .execute();
    }

    public InteractiveVenue getInteractiveVenue(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, INTERACTIVE_VENUE)
                .pathParams(venueTemplateId)
                .execute(InteractiveVenue.class);
    }

    public void updateInteractiveVenue(Long venueTemplateId,
                                       UpdateInteractiveVenue requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, INTERACTIVE_VENUE)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(requestDTO))
                .execute();
    }

    public ExportProcess generateVenueTemplateSectorsReport(Long venueTemplateId, ExportFilter<VenueTemplatesSectorExportFileField> filter) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_REPORT + SECTORS)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(filter))
                .execute(ExportProcess.class);
    }

    public ExportProcess generateVenueTemplateSeatsReport(Long venueTemplateId, ExportFilter<VenueTemplatesSeatExportFileField> filter) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_REPORT + SEATS)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(filter))
                .execute(ExportProcess.class);
    }

    public ExportProcess generateVenueTemplateViewsReport(Long venueTemplateId, ExportFilter filter) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_REPORT + VIEWS)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(filter))
                .execute(ExportProcess.class);
    }

    public ExportProcess getVenueTemplatesReportStatus(Long venueTemplateId, String exportId, Long userId, String type) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_REPORT + "/" + type + "/{exportId}/users/{userId}/status")
                .pathParams(venueTemplateId, exportId, userId)
                .execute(ExportProcess.class);
    }

    public List<DynamicTagGroup> getVenueTemplateDynamicTagGroups(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_DYNAMIC_TAG_GROUPS)
                .pathParams(venueTemplateId)
                .execute(ListType.of(DynamicTagGroup.class));
    }

    public Long createVenueTemplateDynamicTagGroup(Long venueTemplateId, DynamicTagGroup request) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_DYNAMIC_TAG_GROUPS)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(request))
                .execute(Long.class);
    }

    public void updateVenueTemplateDynamicTagGroup(Long venueTemplateId, Long tagGroupId, DynamicTagGroup request) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_DYNAMIC_TAG_GROUP)
                .pathParams(venueTemplateId, tagGroupId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteVenueTemplateDynamicTagGroup(Long venueTemplateId, Long tagGroupId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_DYNAMIC_TAG_GROUP)
                .pathParams(venueTemplateId, tagGroupId)
                .execute();
    }

    public List<DynamicTag> getVenueTemplateDynamicTagGroupTags(Long venueTemplateId, Long tagGroupId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_DYNAMIC_TAG_GROUP_TAGS)
                .pathParams(venueTemplateId, tagGroupId)
                .execute(ListType.of(DynamicTag.class));
    }

    public Long createDynamicTagGroupTag(Long venueTemplateId, Long tagGroupId, CreateVenueTagConfigRequestDTO requestDTO) {
        return httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_DYNAMIC_TAG_GROUP_TAGS)
                .pathParams(venueTemplateId, tagGroupId)
                .body(new ClientRequestBody(buildTagRequestBody(requestDTO)))
                .execute(Long.class);
    }

    public void updateDynamicTagGroupTag(Long venueTemplateId, Long tagGroupId, Long tagId, VenueTagConfigRequestDTO requestDTO) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_DYNAMIC_TAG_GROUP_TAGS + "/{tagId}")
                .pathParams(venueTemplateId, tagGroupId, tagId)
                .body(new ClientRequestBody(buildTagRequestBody(requestDTO)))
                .execute();
    }

    public void deleteDynamicTagGroupTag(Long venueTemplateId, Long tagGroupId, Long tagId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_DYNAMIC_TAG_GROUP_TAGS + "/{tagId}")
                .pathParams(venueTemplateId, tagGroupId, tagId)
                .execute();
    }

    public List<PriceTypeTicketCommunicationElement> getPriceTypePdfTicketCommunicationElements(Long venueTemplateId, Long priceTypeId, PriceTypeCommunicationElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, PDF_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(PriceTypeTicketCommunicationElement.class));
    }

    public List<PriceTypeTicketCommunicationElement> getPriceTypePrinterTicketCommunicationElements(Long venueTemplateId, Long priceTypeId, PriceTypeCommunicationElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, PRINTER_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(PriceTypeTicketCommunicationElement.class));
    }

    public List<PriceTypeTicketCommunicationElement> getPriceTypePassbookTicketCommunicationElements(Long venueTemplateId, Long priceTypeId, PriceTypeCommunicationElementFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, PASSBOOK_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(PriceTypeTicketCommunicationElement.class));
    }

    public void updatePriceTypePdfTicketCommunicationElements(Long venueTemplateId, Long priceTypeId, Set<PriceTypeTicketCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, PDF_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void updatePriceTypePrinterTicketCommunicationElements(Long venueTemplateId, Long priceTypeId, Set<PriceTypeTicketCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, PRINTER_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void updatePriceTypePassbookTicketCommunicationElements(Long venueTemplateId, Long priceTypeId, Set<PriceTypeTicketCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, PASSBOOK_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(venueTemplateId, priceTypeId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void deletePriceTypePdfTicketCommunicationElement(Long venueTemplateId, Long priceTypeId, String language, String tag) {
        httpClient.buildRequest(HttpMethod.DELETE, PDF_TICKET_COMMUNICATION_ELEMENTS + "/languages/{language}/types/{tag}")
                .pathParams(venueTemplateId, priceTypeId, language, tag)
                .execute();
    }

    public void deletePriceTypePrinterTicketCommunicationElement(Long venueTemplateId, Long priceTypeId, String language, String tag) {
        httpClient.buildRequest(HttpMethod.DELETE, PRINTER_TICKET_COMMUNICATION_ELEMENTS + "/languages/{language}/types/{tag}")
                .pathParams(venueTemplateId, priceTypeId, language, tag)
                .execute();
    }

    public void deletePriceTypePassbookTicketCommunicationElement(Long venueTemplateId, Long priceTypeId, String language, String tag) {
        httpClient.buildRequest(HttpMethod.DELETE, PASSBOOK_TICKET_COMMUNICATION_ELEMENTS + "/languages/{language}/types/{tag}")
                .pathParams(venueTemplateId, priceTypeId, language, tag)
                .execute();
    }

    public List<IdNameDTO> findChangedPriceTypeTicketContents(Long venueTemplateId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANGED_TICKET_COMMUNICATION_ELEMENTS)
                .pathParams(venueTemplateId)
                .execute(ListType.of(IdNameDTO.class));
    }

    public IdDTO createVenue(VenueItemPostRequest newVenue) {
        return httpClient.buildRequest(HttpMethod.POST, VENUES)
                .body(new ClientRequestBody(newVenue))
                .execute(IdDTO.class);
    }

    public void updateVenue(VenueItemPutRequest patchedVenue) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE)
                .pathParams(patchedVenue.getId())
                .body(new ClientRequestBody(patchedVenue))
                .execute();
    }

    public void deleteVenueById(Long venueId, Long entityId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE)
                .pathParams(venueId)
                .params(new QueryParameters.Builder().addQueryParameter("entityId", entityId).build())
                .execute();
    }

    public VenueSpaces getVenueSpaces(Long venueId) {
        return httpClient.buildRequest(HttpMethod.GET, SPACES)
                .pathParams(venueId)
                .execute(VenueSpaces.class);
    }

    public VenueSpace getVenueSpace(Long venueId, Long spaceId) {
        return httpClient.buildRequest(HttpMethod.GET, SPACE_ID)
                .pathParams(venueId, spaceId)
                .execute(VenueSpace.class);
    }

    public IdDTO createVenueSpace(VenueSpace newSpace) {
        return httpClient.buildRequest(HttpMethod.POST, SPACES)
                .pathParams(newSpace.getVenueId())
                .body(new ClientRequestBody(newSpace))
                .execute(IdDTO.class);
    }

    public void updateVenueSpace(VenueSpace patchedSpace) {
        httpClient.buildRequest(HttpMethod.PUT, SPACE_ID)
                .pathParams(patchedSpace.getVenueId(), patchedSpace.getId())
                .body(new ClientRequestBody(patchedSpace))
                .execute();
    }

    public void deleteVenueSpace(Long venueId, Long spaceId) {
        httpClient.buildRequest(HttpMethod.DELETE, SPACE_ID)
                .pathParams(venueId, spaceId)
                .execute();
    }

    public TemplateInfoListResponse searchVenueTemplateElementsInfo(Long venueTemplateId, VenueTemplateElementInfoSearchDTO searchDTO) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_ELEMENTS_INFO)
                .pathParams(venueTemplateId)
                .params(new QueryParameters.Builder().addQueryParameters(searchDTO).build())
                .execute(TemplateInfoListResponse.class);
    }

    public TemplateInfoBaseResponse getVenueTemplateElementsInfo(Long venueTemplateId, ElementType elementType, Long elementId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_ELEMENT_INFO)
                .pathParams(venueTemplateId, elementType, elementId)
                .execute(TemplateInfoBaseResponse.class);
    }

    public void createTemplateElementInfo(Long venueTemplateId, TemplateInfoCreateRequest request) {
        httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_ELEMENTS_INFO)
                .pathParams(venueTemplateId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void bulkUpdateTemplateElementInfo(Long venueTemplateId, TemplateInfoBulkUpdateRequest request, VenueTemplateElementInfoSearchDTO requestSearch) {
        QueryParameters.Builder paramsBuilder = new QueryParameters.Builder();
        if (requestSearch.getType() != null) {
            paramsBuilder.addQueryParameter("type", requestSearch.getType().name());
        }
        if (StringUtils.isNotBlank(requestSearch.getQ())) {
            paramsBuilder.addQueryParameter("q", requestSearch.getQ());
        }
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_ELEMENTS_INFO)
                .pathParams(venueTemplateId)
                .params(paramsBuilder.build())
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void bulkDeleteVenueTemplateElementInfo(Long venueTemplateId, VenueTemplateElementInfoBulkRequestDTO filters) {
        QueryParameters.Builder paramsBuilder = new QueryParameters.Builder();
        if (CollectionUtils.isNotEmpty(filters.getElements())) {
            paramsBuilder.addQueryParameter("itemsToDelete", filters.getElements());
        }
        if (filters.getAllElements() != null) {
            paramsBuilder.addQueryParameter("deleteAllTemplateInfo", filters.getAllElements());
        }
        if (StringUtils.isNotBlank(filters.getQ())) {
            paramsBuilder.addQueryParameter("q", filters.getQ());
        }
        if (filters.getType() != null) {
            paramsBuilder.addQueryParameter("type", filters.getType().name());
        }
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_ELEMENTS_INFO)
                .pathParams(venueTemplateId)
                .params(paramsBuilder.build())
                .execute();
    }

    public void updateTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId, UpdateTemplateInfoDefault updateTemplateInfoDefault) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_ELEMENT_INFO)
                .pathParams(venueTemplateId, elementType, elementId)
                .body(new ClientRequestBody(updateTemplateInfoDefault))
                .execute();
    }

    public void deleteTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_ELEMENT_INFO)
                .pathParams(venueTemplateId, elementType, elementId)
                .execute();
    }

    public void deleteTemplateElementInfoImages(Long venueTemplateId, ElementType elementType, Long elementId,
                                                ElementInfoImageType imageType, String language,
                                                TemplateInfoImagesDeleteFilterDTO filter) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_ELEMENT_INFO + "/images/{imageType}/languages/{language}")
                .pathParams(venueTemplateId, elementType, elementId, imageType, language)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute();
    }

    public SessionTemplateInfoListResponse searchSessionVenueTemplateElementsInfo(Long venueTemplateId, Long sessionId,
                                                                                  VenueTemplateSessionElementInfoSearchDTO searchDTO) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_SESSIONS_ELEMENTS_INFO)
                .pathParams(venueTemplateId, sessionId)
                .params(new QueryParameters.Builder().addQueryParameters(searchDTO).build())
                .execute(SessionTemplateInfoListResponse.class);
    }

    public void createSessionTemplateElementInfo(Long venueTemplateId, Long sessionId, SessionTemplateInfoRequest request) {
        httpClient.buildRequest(HttpMethod.POST, VENUE_TEMPLATE_SESSIONS_ELEMENTS_INFO)
                .pathParams(venueTemplateId, sessionId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void bulkUpdateSessionTemplateElementInfo(Long venueTemplateId, Long sessionId, SessionTemplateInfoBulkUpdateRequest request,
                                                     VenueTemplateElementInfoSearchDTO requestSearch) {
        QueryParameters.Builder paramsBuilder = new QueryParameters.Builder();
        if (StringUtils.isNotBlank(requestSearch.getQ())) {
            paramsBuilder.addQueryParameter("q", requestSearch.getQ());
        }
        if (requestSearch.getType() != null) {
            paramsBuilder.addQueryParameter("type", requestSearch.getType().name());
        }
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_SESSIONS_ELEMENTS_INFO)
                .pathParams(venueTemplateId, sessionId)
                .params(paramsBuilder.build())
                .body(new ClientRequestBody(request))
                .execute();
    }

    public SessionTemplateInfoResponse getSessionVenueTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_TEMPLATE_SESSIONS_ELEMENT_INFO)
                .pathParams(venueTemplateId, elementType, elementId, sessionId)
                .execute(SessionTemplateInfoResponse.class);
    }

    public void updateSessionVenueTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId, Long sessionId, SessionUpdateTemplateInfo request) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_SESSIONS_ELEMENT_INFO)
                .pathParams(venueTemplateId, elementType, elementId, sessionId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteSessionVenueTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId, Long sessionId) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_SESSIONS_ELEMENT_INFO)
                .pathParams(venueTemplateId, elementType, elementId, sessionId)
                .execute();
    }

    public void updateStatusSessionVenueTemplateElementInfo(Long venueTemplateId, ElementType elementType, Long elementId, Long sessionId,
                                                            SessionTemplateInfoStatusUpdateRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_TEMPLATE_SESSIONS_ELEMENT_INFO_STATUS)
                .pathParams(venueTemplateId, elementType, elementId, sessionId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteSessionTemplateElementInfoImages(Long venueTemplateId, ElementType elementType, Long elementId,
                                                       Long sessionId, ElementInfoImageType imageType, String language,
                                                       TemplateInfoImagesDeleteFilterDTO filter) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_TEMPLATE_SESSIONS_ELEMENT_INFO + "/images/{imageType}/languages/{language}")
                .pathParams(venueTemplateId, elementType, elementId, sessionId, imageType, language)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute();
    }

    public List<IdNameCodeDTO> getProviderVenues(String provider) {
        return httpClient.buildRequest(HttpMethod.GET, "/providers/{providerId}/venues")
                .pathParams(provider)
                .execute(ListType.of(IdNameCodeDTO.class));
    }

    public List<IdNameCodeDTO> getProviderVenueTemplates(String provider, Long externalVenueId) {
        QueryParameters.Builder paramsBuilder = new QueryParameters.Builder();
        if (externalVenueId != null) {
            paramsBuilder.addQueryParameter("externalVenueId", externalVenueId);
        }
        return httpClient.buildRequest(HttpMethod.GET, "/providers/{providerId}/venue-templates")
                .pathParams(provider)
                .params(paramsBuilder.build())
                .execute(ListType.of(IdNameCodeDTO.class));
    }

}
