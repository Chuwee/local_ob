package es.onebox.mgmt.datasources.ms.ticket;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.CodeDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.datasources.common.dto.QuotaCapacity;
import es.onebox.mgmt.datasources.ms.ticket.dto.AvailablePassbookField;
import es.onebox.mgmt.datasources.ms.ticket.dto.CapacityExportFilter;
import es.onebox.mgmt.datasources.ms.ticket.dto.CapacityRelocationRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.CreatePassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.NotNumberedZoneCapacityBulk;
import es.onebox.mgmt.datasources.ms.ticket.dto.NotNumberedZoneLinkDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookRequestFilter;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplateList;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketLinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketNotNumberedZoneLinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketNotNumberedZoneUnlinkResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketSeatLink;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeasonTicketSeatsSummary;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeatCapacityBulk;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeatLinkDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionOccupationDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionOccupationsSearchRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionPriceZoneOccupationResponseDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreview;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPrintResult;
import es.onebox.mgmt.datasources.ms.ticket.dto.UpdatePassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.WhitelistSearchResponse;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.Seat;
import es.onebox.mgmt.datasources.ms.ticket.dto.availability.Sector;
import es.onebox.mgmt.datasources.ms.ticket.enums.PassbookTemplateType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagNotNumberedZoneDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagSeatDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtExportsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtPassbookErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.mgmt.sessions.dto.WhiteListExportFileField;
import es.onebox.mgmt.sessions.dto.WhitelistFilter;
import es.onebox.servicepreview.core.context.ServicePreviewContext;
import es.onebox.tracer.okhttp.TracingInterceptor;
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

@Service
public class MsTicketDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/tickets-api/" + API_VERSION;

    private static final String EVENT = "/events/{eventId}";
    private static final String SESSION = "/sessions/{sessionId}";
    private static final String SESSIONS = "/sessions";
    private static final String OCCUPATION = SESSION + "/occupation";
    private static final String CAPACITY = "/capacity";
    private static final String CAPACITY_PATH = EVENT + SESSION + CAPACITY;
    private static final String CAPACITY_BULK_PATH = EVENT + SESSIONS + CAPACITY;
    private static final String QUOTA_CAPACITY_PATH = EVENT + SESSION + CAPACITY + "/quotas";
    private static final String RELOCATION_CAPACITY_PATH = EVENT + SESSION + CAPACITY + "/relocation";
    private static final String SEASON_TICKET_SEATS_SESSION_PATH = "/season-tickets/{sessionId}";
    private static final String PASSBOOK_PREVIEW = "/passbook/{passbookId}/preview";
    private static final String PASSBOOK_TEMPLATES = "/passbook-templates";
    private static final String PASSBOOK_TEMPLATE = PASSBOOK_TEMPLATES + "/{code}";
    private static final String PASSBOOK_LITERALS = PASSBOOK_TEMPLATE + "/literals/{langCode}";
    private static final String PASSBOOK_AVAILABLE_FIELDS = PASSBOOK_TEMPLATES + "/availableFields";
    private static final String PASSBOOK_AVAILABLE_DATA_PLACEHOLDERS = PASSBOOK_TEMPLATES + "/availableDataPlaceholders";
    private static final String PASSBOOK_AVAILABLE_LITERALS = PASSBOOK_TEMPLATES + "/availableLiterals";
    private static final String WHITELIST = SESSION + "/whitelist";
    private static final String WHITELIST_REPORT = WHITELIST + "/exports";
    private static final String WHITELIST_REPORT_STATUS = WHITELIST_REPORT + "/{exportId}/users/{userId}/status";
    private static final String AVAILABILITY_PATH = "/sessions/{sessionId}/availability";
    private static final String CAPACITY_TREE_AVAILABILITY_PATH = AVAILABILITY_PATH + "/capacity-tree";
    private static final String SEATS_AVAILABILITY_PATH = AVAILABILITY_PATH + "/row/{rowId}/seats";
    private static final String NOT_NUMBERED_ZONE_SEATS_AVAILABILITY_PATH = AVAILABILITY_PATH + "/not-numbered-zones/{notNumberedZoneId}/seats";
    private static final String SESSION_SALES = SESSION + "/sales";
    private static final String TICKET_PREVIEW = "/ticket-preview";
    private static final String SESSION_CAPACITY_REPORT = CAPACITY_PATH+ "/exports";
    private static final String SESSION_CAPACITY_REPORT_STATUS = SESSION_CAPACITY_REPORT + "/{exportId}/users/{userId}/status";

    private static final Map<String, ErrorCode> ERROR_CODES;

    private static final int TIMEOUT = 60000;

    private final String baseUrl;

    static {
        ERROR_CODES = new HashMap<>();
        ERROR_CODES.put("400G0001", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("MTK0000", ApiMgmtErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("MTK0001", ApiMgmtErrorCode.VALIDATE_TICKET_EXCEPTION);
        ERROR_CODES.put("MTK0002", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("MTK0003", ApiMgmtSessionErrorCode.SESSION_NOT_FOUND);
        ERROR_CODES.put("MTK0004", ApiMgmtErrorCode.BARCODES_NOT_UPDATED);
        ERROR_CODES.put("MTK0005", ApiMgmtErrorCode.REQUEST_CONFLICT);
        ERROR_CODES.put("MTK0006", ApiMgmtErrorCode.SEASON_SESSION_EXCEPTION);
        ERROR_CODES.put("MTK0007", ApiMgmtSessionErrorCode.INVALID_IDS_FOR_SESSION);
        ERROR_CODES.put("MTK0008", ApiMgmtErrorCode.SESSION_CAPACITY_COUNTER);
        ERROR_CODES.put("MTK0009", ApiMgmtErrorCode.SESSION_PACK_INVALID_TYPE);
        ERROR_CODES.put("MTK0010", ApiMgmtErrorCode.SESSION_PACK_SEATS);
        ERROR_CODES.put("MTK0011", ApiMgmtErrorCode.SESSION_PACK_WITHOUT_SESSIONS);
        ERROR_CODES.put("MTK0012", ApiMgmtErrorCode.SESSION_PACK_WITHOUT_CAPACITY);
        ERROR_CODES.put("MTK0017", ApiMgmtErrorCode.FREE_SESSION_PACK_SEAT_CANT_BE_CHANGED);
        ERROR_CODES.put("SESSION_PACK_LINK_SEATS", ApiMgmtSessionErrorCode.SESSION_PACK_LINK_SEATS);
        ERROR_CODES.put("SEAT_INVALID_STATUS", ApiMgmtSessionErrorCode.SEAT_INVALID_STATUS);
        ERROR_CODES.put("EVENT_SESSION_CAPACITY_LOCK", ApiMgmtSessionErrorCode.EVENT_SESSION_CAPACITY_LOCK);
        ERROR_CODES.put("SEAT_STATUS_UPDATE_BLOCKED_EXTERNAL_NOT_ALLOWED", ApiMgmtErrorCode.SEAT_STATUS_UPDATE_BLOCKED_EXTERNAL_NOT_ALLOWED);
        ERROR_CODES.put("SEAT_PRICE_TYPE_UPDATE_AVET_NOT_ALLOWED", ApiMgmtErrorCode.SEAT_PRICE_TYPE_UPDATE_AVET_NOT_ALLOWED);
        ERROR_CODES.put("INVALID_ZONE_COUNTERS_SETTING", ApiMgmtErrorCode.INVALID_ZONE_COUNTERS_SETTING);
        ERROR_CODES.put("UPDATE_SEATS_LIMIT_EXCEED", ApiMgmtErrorCode.UPDATE_SEATS_LIMIT_EXCEED);

        ERROR_CODES.put("PASSBOOK_DATA_MANDATORY", ApiMgmtPassbookErrorCode.PASSBOOK_DATA_MANDATORY);
        ERROR_CODES.put("PASSBOOK_ENTITY_ID_MANDATORY", ApiMgmtPassbookErrorCode.PASSBOOK_ENTITY_ID_MANDATORY);
        ERROR_CODES.put("PASSBOOK_CODE_MANDATORY", ApiMgmtPassbookErrorCode.PASSBOOK_CODE_MANDATORY);
        ERROR_CODES.put("PASSBOOK_NAME_MANDATORY", ApiMgmtPassbookErrorCode.PASSBOOK_NAME_MANDATORY);
        ERROR_CODES.put("PASSBOOK_ALREADY_EXISTS", ApiMgmtPassbookErrorCode.PASSBOOK_ALREADY_EXISTS);
        ERROR_CODES.put("PASSBOOK_CODE_UNACCEPTABLE", ApiMgmtPassbookErrorCode.PASSBOOK_CODE_UNACCEPTABLE);
        ERROR_CODES.put("PASSBOOK_BASE_TEMPLATE_NOT_FOUND", ApiMgmtPassbookErrorCode.PASSBOOK_BASE_TEMPLATE_NOT_FOUND);
        ERROR_CODES.put("PASSBOOK_OPERATOR_ID_MANDATORY", ApiMgmtPassbookErrorCode.PASSBOOK_OPERATOR_ID_MANDATORY);
        ERROR_CODES.put("PASSBOOK_LANG_MANDATORY", ApiMgmtPassbookErrorCode.PASSBOOK_OPERATOR_ID_MANDATORY);
        ERROR_CODES.put("PASSBOOK_TEMPLATE_NOT_FOUND", ApiMgmtPassbookErrorCode.PASSBOOK_TEMPLATE_NOT_FOUND);
        ERROR_CODES.put("DELETE_DEFAULT_PASSBOOK", ApiMgmtPassbookErrorCode.DELETE_DEFAULT_TEMPLATE);
        ERROR_CODES.put("INVALID_LITERAL_KEY", ApiMgmtPassbookErrorCode.INVALID_LITERAL_KEY);
        ERROR_CODES.put("INVALID_PASSBOOK_TEMPLATE_FIELD", ApiMgmtPassbookErrorCode.INVALID_PASSBOOK_FIELD);
        ERROR_CODES.put("INVALID_PASSBOOK_TEMPLATE", ApiMgmtPassbookErrorCode.INVALID_PASSBOOK_TEMPLATE);
        ERROR_CODES.put("MEMBER_ORDER_TEMPLATE_DEFAULT", ApiMgmtPassbookErrorCode.MEMBER_ORDER_TEMPLATE_DEFAULT);
        ERROR_CODES.put("DELETE_LAST_IN_TYPE_FOR_ENTITY", ApiMgmtPassbookErrorCode.DELETE_LAST_IN_TYPE_FOR_ENTITY);
        ERROR_CODES.put("MEMBER_ORDER_TEMPLATE_NOT_ALLOWED_IN_ENTITY", ApiMgmtPassbookErrorCode.MEMBER_ORDER_TEMPLATE_NOT_ALLOWED_IN_ENTITY);
        ERROR_CODES.put("DIGITAL_SEASON_TICKET_NOT_ALLOWED_IN_ENTITY", ApiMgmtPassbookErrorCode.DIGITAL_SEASON_TICKET_NOT_ALLOWED_IN_ENTITY);

        ERROR_CODES.put("AVAILABILITY_ROW_IS_NOT_FROM_SESSION", ApiMgmtErrorCode.AVAILABILITY_ROW_IS_NOT_FROM_SESSION);
        ERROR_CODES.put("AVAILABILITY_NOT_NUMBERED_ZONE_IS_NOT_FROM_SESSION", ApiMgmtErrorCode.AVAILABILITY_ROW_IS_NOT_FROM_SESSION);
        ERROR_CODES.put("EXPORT_LIMIT_REACHED", ApiMgmtExportsErrorCode.EXPORT_LIMIT_REACHED);
        ERROR_CODES.put("PASSBOOK_RESOURCE_LOAD_FAILURE", ApiMgmtPassbookErrorCode.PASSBOOK_RESOURCE_LOAD_FAILURE);

        ERROR_CODES.put("TICKET_REPORT_NOT_GENERATED", ApiMgmtErrorCode.TICKET_REPORT_NOT_GENERATED);
        ERROR_CODES.put("UNSUPPORTED_TICKET_LANGUAGE", ApiMgmtErrorCode.NOT_AVAILABLE_LANG);
        ERROR_CODES.put("TICKET_TEMPLATE_NOT_FOUND", ApiMgmtErrorCode.TICKET_TEMPLATE_NOT_FOUND);
        ERROR_CODES.put("EVENT_NOT_FOUND", ApiMgmtErrorCode.EVENT_NOT_FOUND);
        ERROR_CODES.put("SESSION_NOT_FOUND", ApiMgmtErrorCode.SESSION_NOT_FOUND);
        ERROR_CODES.put("ORDER_NOT_FOUND", ApiMgmtErrorCode.ORDER_NOT_FOUND);
        ERROR_CODES.put("MTK0015", ApiMgmtErrorCode.TICKET_NOT_FOUND);
        ERROR_CODES.put("RELOCATION_SEAT_IS_NUMBERED_ZONE", ApiMgmtErrorCode.RELOCATION_SEAT_IS_NUMBERED_ZONE);
        ERROR_CODES.put("RELOCATION_INVALID_ORDER_STATUS_TYPE", ApiMgmtErrorCode.RELOCATION_INVALID_ORDER_STATUS_TYPE);
        ERROR_CODES.put("RELOCATION_INVALID_ORDER_STATUS_STATE", ApiMgmtErrorCode.RELOCATION_INVALID_ORDER_STATUS_STATE);
        ERROR_CODES.put("RELOCATION_INVALID_SOURCE_SEAT_STATUS", ApiMgmtErrorCode.RELOCATION_INVALID_SOURCE_SEAT_STATUS);
        ERROR_CODES.put("RELOCATION_INVALID_SOURCE_SEAT_WITHOUT_ORDER_CODE", ApiMgmtErrorCode.RELOCATION_INVALID_SOURCE_SEAT_WITHOUT_ORDER_CODE);
        ERROR_CODES.put("RELOCATION_INVALID_DESTINATION_SEAT_WITH_ORDER_CODE", ApiMgmtErrorCode.RELOCATION_INVALID_DESTINATION_SEAT_WITH_ORDER_CODE);
        ERROR_CODES.put("RELOCATION_INVALID_DESTINATION_SEAT_STATUS", ApiMgmtErrorCode.RELOCATION_INVALID_DESTINATION_SEAT_STATUS);
        ERROR_CODES.put("RELOCATION_INVALID_SESSION_TYPE", ApiMgmtErrorCode.RELOCATION_INVALID_SESSION_TYPE);
        ERROR_CODES.put("RELOCATION_INVALID_SESSION_STATUS", ApiMgmtErrorCode.RELOCATION_INVALID_SESSION_STATUS);
        ERROR_CODES.put("RELOCATION_INVALID_EVENT_TYPE", ApiMgmtErrorCode.RELOCATION_INVALID_EVENT_TYPE);
        ERROR_CODES.put("RELOCATION_SEAT_IS_SOURCE_AND_DESTINATION", ApiMgmtErrorCode.RELOCATION_SEAT_IS_SOURCE_AND_DESTINATION);
        ERROR_CODES.put("RELOCATION_REPEATED_SOURCE_SEAT", ApiMgmtErrorCode.RELOCATION_REPEATED_SOURCE_SEAT);
        ERROR_CODES.put("RELOCATION_REPEATED_DESTINATION_SEAT", ApiMgmtErrorCode.RELOCATION_REPEATED_DESTINATION_SEAT);
    }

    private final CloseableHttpClient basicHttpClient;
    private final HttpClient httpClient;

    @Autowired
    public MsTicketDatasource(@Value("${clients.services.ms-ticket}") String baseUrl,
                              ObjectMapper jacksonMapper,
                              TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
                .build();

        this.basicHttpClient = HttpClients.createDefault();
        this.baseUrl = baseUrl + BASE_PATH;
    }

    public SessionOccupationDTO getSessionOccupation(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, OCCUPATION)
                .pathParams(sessionId)
                .execute(SessionOccupationDTO.class);
    }

    public List<SessionPriceZoneOccupationResponseDTO> searchSessionOccupationsByPriceZones(SessionOccupationsSearchRequest request) {
        return httpClient.buildRequest(HttpMethod.POST, "/session-occupations/price-zones/search")
                .body(new ClientRequestBody(request))
                .execute(ListType.of(SessionPriceZoneOccupationResponseDTO.class));
    }

    public SessionOccupationDTO getSessionGroupOccupation(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, OCCUPATION + "/groups")
                .pathParams(sessionId)
                .execute(SessionOccupationDTO.class);
    }

    public InputStream getCapacityMap(Long seasonTicketId, Long sessionId) {
        String url = baseUrl + "/events/" + seasonTicketId + SESSIONS + "/" + sessionId + CAPACITY;
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
            throw new OneboxRestException(ApiMgmtErrorCode.GENERIC_ERROR, "Request problem session capacity with id: " + sessionId, e);
        }
    }

    public void updateSeatsCapacity(Long eventId, Long sessionId, VenueTagSeatDTO[] tags) {
        httpClient.buildRequest(HttpMethod.PUT, CAPACITY_PATH + "/seats")
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(tags))
                .execute();
    }

    public void updateSeatsCapacityBulk(Long eventId, List<Long> sessionIds, List<VenueTagSeatDTO> tags) {
        httpClient.buildRequest(HttpMethod.PUT, CAPACITY_BULK_PATH + "/seats/bulk")
                .pathParams(eventId)
                .body(new ClientRequestBody(new SeatCapacityBulk(sessionIds, tags)))
                .execute();
    }

    public void updateNNZonesCapacity(Long eventId, Long sessionId, VenueTagNotNumberedZoneDTO[] tags) {
        httpClient.buildRequest(HttpMethod.PUT, CAPACITY_PATH + "/not-numbered-zones")
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(tags))
                .execute();
    }

    public void updateNNZonesCapacityBulk(Long eventId, List<Long> sessionIds, List<VenueTagNotNumberedZoneDTO> tags) {
        httpClient.buildRequest(HttpMethod.PUT, CAPACITY_BULK_PATH + "/not-numbered-zones/bulk")
                .pathParams(eventId)
                .body(new ClientRequestBody(new NotNumberedZoneCapacityBulk(sessionIds, tags)))
                .execute();
    }

    public Boolean getSessionCapacityUpdating(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT + SESSION + CAPACITY + "/updating")
                .pathParams(eventId, sessionId)
                .execute(Boolean.class);
    }


    public List<Long> getEventSessionsCapacityUpdating(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT + SESSIONS + CAPACITY + "/updating")
                .pathParams(eventId)
                .execute(ListType.of(Long.class));
    }

    public Boolean getSessionCapacityGenerating(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT + SESSION + CAPACITY + "/generating")
                .pathParams(eventId, sessionId)
                .execute(Boolean.class);
    }

    public List<Long> getEventSessionsCapacityGenerating(Long eventId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT + SESSIONS + CAPACITY + "/generating")
                .pathParams(eventId)
                .execute(ListType.of(Long.class));
    }


    public List<Long> linkSeats(Long eventId, Long sessionId, SeatLinkDTO seatLinks) {
        return httpClient.buildRequest(HttpMethod.POST, CAPACITY_PATH + "/seats/link")
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(seatLinks))
                .execute(ListType.of(Long.class));
    }

    public List<Long> unlinkSeats(Long eventId, Long sessionId, SeatLinkDTO seatLinks) {
        return httpClient.buildRequest(HttpMethod.POST, CAPACITY_PATH + "/seats/unlink")
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(seatLinks))
                .execute(ListType.of(Long.class));
    }

    public void linkNNZ(Long eventId, Long sessionId, NotNumberedZoneLinkDTO nnzLinks) {
        httpClient.buildRequest(HttpMethod.POST, CAPACITY_PATH + "/not-numbered-zones/link")
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(nnzLinks))
                .execute();
    }

    public void unlinkNNZ(Long eventId, Long sessionId, NotNumberedZoneLinkDTO nnzLinks) {
        httpClient.buildRequest(HttpMethod.POST, CAPACITY_PATH + "/not-numbered-zones/unlink")
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(nnzLinks))
                .execute();
    }

    public SeasonTicketLinkResponse seasonTicketLinkSeats(Long sessionId, SeasonTicketSeatLink seasonTicketSeatLink) {
        return httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_SEATS_SESSION_PATH + "/seats/link")
                .pathParams(sessionId)
                .body(new ClientRequestBody(seasonTicketSeatLink))
                .execute(SeasonTicketLinkResponse.class);
    }

    public SeasonTicketLinkResponse seasonTicketUnLinkSeats(Long sessionId, SeasonTicketSeatLink seasonTicketSeatLink) {
        return httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_SEATS_SESSION_PATH + "/seats/unlink")
                .pathParams(sessionId)
                .body(new ClientRequestBody(seasonTicketSeatLink))
                .execute(SeasonTicketLinkResponse.class);
    }

    public TicketPrintResult getPassbookPreview(PassbookPreviewRequest request, String passbookCode) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, PASSBOOK_PREVIEW)
                .pathParams(passbookCode)
                .params(params)
                .execute(TicketPrintResult.class);
    }

    public PassbookTemplateList searchPassbookTemplates(PassbookRequestFilter filter) {
        return httpClient.buildRequest(HttpMethod.GET, PASSBOOK_TEMPLATES)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(PassbookTemplateList.class);
    }

    public CodeDTO createPassbookTemplate(CreatePassbookTemplate cpt) {
        return httpClient.buildRequest(HttpMethod.POST, PASSBOOK_TEMPLATES)
                .body(new ClientRequestBody(cpt))
                .execute(CodeDTO.class);
    }

    public void deletePassbookTemplate(String code, Long entityId) {
        httpClient.buildRequest(HttpMethod.DELETE, PASSBOOK_TEMPLATE)
                .pathParams(code)
                .params(new QueryParameters.Builder().addQueryParameter("entityId", entityId).build())
                .execute();
    }

    public PassbookTemplate getPassbookTemplate(String code, Long entityId) {
        return httpClient.buildRequest(HttpMethod.GET, PASSBOOK_TEMPLATE)
                .pathParams(code)
                .params(new QueryParameters.Builder().addQueryParameter("entityId", entityId).build())
                .execute(PassbookTemplate.class);
    }

    public void updatePassbookTemplate(String code, Long entityId, UpdatePassbookTemplate updatePassbookTemplate) {
        httpClient.buildRequest(HttpMethod.PUT, PASSBOOK_TEMPLATE)
                .pathParams(code)
                .params(new QueryParameters.Builder().addQueryParameter("entityId", entityId).build())
                .body(new ClientRequestBody(updatePassbookTemplate))
                .execute();
    }

    public Map<String, String> getPassbookLiterals(Long entityId, String code, String langCode) {
        return httpClient.buildRequest(HttpMethod.GET, PASSBOOK_LITERALS)
                .pathParams(code, langCode)
                .params(new QueryParameters.Builder().addQueryParameter("entityId", entityId).build())
                .execute(HashMap.class);
    }

    public void updatePassbookLiterals(String passbookCode, Long entityId, String langCode, Map<String, String> literals) {
        httpClient.buildRequest(HttpMethod.PUT, PASSBOOK_LITERALS)
                .pathParams(passbookCode, langCode)
                .params(new QueryParameters.Builder().addQueryParameter("entityId", entityId).build())
                .body(new ClientRequestBody(literals))
                .execute();
    }

    public List<AvailablePassbookField> availablePassbookFields(PassbookTemplateType type) {
        QueryParameters.Builder paramBuilder = new QueryParameters.Builder();
        if (type != null) {
            paramBuilder.addQueryParameter("type", type);
        }
        return httpClient.buildRequest(HttpMethod.GET, PASSBOOK_AVAILABLE_FIELDS)
                .params(paramBuilder.build())
                .execute(ListType.of(AvailablePassbookField.class));
    }

    public List<QuotaCapacity> getQuotasCapacity(Long eventId, Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, QUOTA_CAPACITY_PATH)
                .pathParams(eventId, sessionId)
                .execute(ListType.of(QuotaCapacity.class));
    }

    public void updateQuotasCapacity(Long eventId, Long sessionId, List<QuotaCapacity> requestDTO, Boolean skipRefreshSession) {
        QueryParameters.Builder paramBuilder = new QueryParameters.Builder();
        paramBuilder.addQueryParameter("skipRefreshSession", skipRefreshSession);
        httpClient.buildRequest(HttpMethod.PUT, QUOTA_CAPACITY_PATH)
                .pathParams(eventId, sessionId)
                .params(paramBuilder.build())
                .body(new ClientRequestBody(requestDTO))
                .execute();
    }

    public ExportProcess generateWhiteListReport(Long sessionId, ExportFilter<WhiteListExportFileField> filter) {
        return httpClient.buildRequest(HttpMethod.POST, WHITELIST_REPORT).pathParams(sessionId).body(new ClientRequestBody(filter))
                .execute(ExportProcess.class);
    }

    public ExportProcess getWhitelistReportStatus(Long sessionId, String exportId, Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, WHITELIST_REPORT_STATUS).pathParams(sessionId, exportId, userId)
                .execute(ExportProcess.class);
    }

    public WhitelistSearchResponse getWhitelist(Long sessionId, WhitelistFilter filter) {
        QueryParameters.Builder paramBuilder = new QueryParameters.Builder();
        paramBuilder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, WHITELIST)
                .pathParams(sessionId)
                .params(paramBuilder.build())
                .execute(WhitelistSearchResponse.class);

    }

    public List<Sector> getAvailableSectorsAndRows(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, CAPACITY_TREE_AVAILABILITY_PATH).pathParams(sessionId)
                .execute(ListType.of(Sector.class));
    }

    public List<Seat> getAvailableSeatsByRow(Long sessionId, Long rowId) {
        return httpClient.buildRequest(HttpMethod.GET, SEATS_AVAILABILITY_PATH).pathParams(sessionId, rowId)
                .execute(ListType.of(Seat.class));
    }

    public List<Seat> getAvailableSeatsByNotNumberedZone(Long sessionId, Long notNumberedZoneId) {
        return httpClient.buildRequest(HttpMethod.GET, NOT_NUMBERED_ZONE_SEATS_AVAILABILITY_PATH)
                .pathParams(sessionId, notNumberedZoneId)
                .execute(ListType.of(Seat.class));
    }

    public Long getSessionSalesAmount(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION_SALES)
                .pathParams(sessionId)
                .execute(Long.class);
    }

    public List<String> availableDataPlaceholders(PassbookTemplateType type) {
        QueryParameters.Builder paramBuilder = new QueryParameters.Builder();
        if (type != null) {
            paramBuilder.addQueryParameter("type", type);
        }
        return httpClient.buildRequest(HttpMethod.GET, PASSBOOK_AVAILABLE_DATA_PLACEHOLDERS)
                .params(paramBuilder.build())
                .execute(ListType.of(String.class));
    }

    public List<String> availableLiteralKeys() {
        return httpClient.buildRequest(HttpMethod.GET, PASSBOOK_AVAILABLE_LITERALS)
                .execute(ListType.of(String.class));
    }

    public TicketPreview getTicketPdfPreview(TicketPreviewRequest request) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, TICKET_PREVIEW)
                .params(params)
                .execute(TicketPreview.class);
    }

    public SeasonTicketNotNumberedZoneLinkResponse linkSeasonTicketNNZ(Long sessionId, SeasonTicketNotNumberedZoneLink seasonTicketNotNumberedZoneLink) {
        return httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_SEATS_SESSION_PATH + "/not-numbered-zones/link")
                .pathParams(sessionId)
                .body(new ClientRequestBody(seasonTicketNotNumberedZoneLink))
                .execute(SeasonTicketNotNumberedZoneLinkResponse.class);
    }

    public SeasonTicketNotNumberedZoneUnlinkResponse unlinkSeasonTicketNNZ(Long sessionId, SeasonTicketNotNumberedZoneLink seasonTicketNotNumberedZoneLink) {
        return httpClient.buildRequest(HttpMethod.POST, SEASON_TICKET_SEATS_SESSION_PATH + "/not-numbered-zones/unlink")
                .pathParams(sessionId)
                .body(new ClientRequestBody(seasonTicketNotNumberedZoneLink))
                .execute(SeasonTicketNotNumberedZoneUnlinkResponse.class);
    }

    public SeasonTicketSeatsSummary getSeasonTicketSeatsSummary(Long sessionId) {
        return httpClient.buildRequest(HttpMethod.GET, SEASON_TICKET_SEATS_SESSION_PATH + "/seats/summary")
                .pathParams(sessionId)
                .execute(SeasonTicketSeatsSummary.class);
    }

    public ExportProcess generateSessionCapacityReport(Long eventId, Long sessionId, CapacityExportFilter filter) {
        return httpClient.buildRequest(HttpMethod.POST, SESSION_CAPACITY_REPORT).pathParams(eventId, sessionId).body(new ClientRequestBody(filter))
                .execute(ExportProcess.class);
    }

    public ExportProcess getSessionCapacityReportStatus(Long eventId, Long sessionId, String exportId, Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, SESSION_CAPACITY_REPORT_STATUS).pathParams(eventId, sessionId, exportId, userId)
                .execute(ExportProcess.class);
    }

    public void relocateSeats(Long eventId, Long sessionId, CapacityRelocationRequest capacityRelocationRequest) {
        httpClient.buildRequest(HttpMethod.POST, RELOCATION_CAPACITY_PATH)
                .pathParams(eventId, sessionId)
                .body(new ClientRequestBody(capacityRelocationRequest))
                .execute();
    }
}
