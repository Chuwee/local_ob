package es.onebox.mgmt.datasources.ms.accesscontrol;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.accesscontrol.enums.AccessControlSystem;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.AddProductEventRequestDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.ExternalBarcodesExportRequest;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.HandlePackageEventRequestDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.ProductResponseDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.SkidataVenueConfig;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.StartImportProcessRequest;
import es.onebox.mgmt.exception.ApiMgmtAccessControlErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MsAccessControlDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/access-control-api/" + API_VERSION;
    private static final int TIMEOUT = 60000;

    private static final String SYSTEMS = "/systems";
    private static final String AC_SYSTEM_ID = SYSTEMS + "/{accessControlSystem}";
    private static final String ENTITIES = AC_SYSTEM_ID + "/entities";
    private static final String ENTITY_ID = ENTITIES + "/{entityId}";
    private static final String VENUES = AC_SYSTEM_ID + "/venues";
    private static final String VENUE_ID = VENUES + "/{venueId}";
    private static final String VENUE_CONFIGURATION = VENUE_ID + "/configuration";
    private static final String EXTERNAL_BARCODES = "/external-barcodes";
    private static final String EXTERNAL_BARCODES_START = EXTERNAL_BARCODES + "/import/start";
    private static final String EXTERNAL_BARCODES_VERIFY = EXTERNAL_BARCODES + "/import/verify";
    private static final String EXTERNAL_BARCODES_PENDING = EXTERNAL_BARCODES + "/import/pending";
    private static final String EXPORT_EXTERNAL_BARCODES = EXTERNAL_BARCODES + "/exports";
    private static final String EXPORT_EXTERNAL_BARCODES_STATUS = EXTERNAL_BARCODES + "/exports/{exportId}/users/{userId}/status";
    private static final String FORTRESS = "/fortress";
    private static final String ENTITY = "/entity/{entityId}";
    private static final String EVENT = "/events/{eventId}";
    private static final String SESSIONS = "/sessions";
    private static final String SEASON_TICKET = "/season-ticket/{seasonTicketId}";
    private static final String FORTRESS_ST_ASSIGN = FORTRESS + ENTITY + SEASON_TICKET + "/assign";
    private static final String FORTRESS_ST_UNASSIGN = FORTRESS + ENTITY + SEASON_TICKET + "/unassign";
    private static final String EVENT_VENUE_TEMPLATE_ID = "/venue/{venueTemplateId}";
    private static final String RATE = "/rates/{rateId}";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("SYSTEM_ENTITY_EXISTS", ApiMgmtAccessControlErrorCode.SYSTEM_ENTITY_EXISTS);
        ERROR_CODES.put("VENUE_SYSTEM_NOT_ASSOCIATED", ApiMgmtAccessControlErrorCode.VENUE_SYSTEM_NOT_ASSOCIATED);
        ERROR_CODES.put("SYSTEM_VENUE_EXISTS", ApiMgmtAccessControlErrorCode.SYSTEM_VENUE_EXISTS);
        ERROR_CODES.put("SKIDATA_CONFIG_ALREADY_EXISTS", ApiMgmtAccessControlErrorCode.SKIDATA_CONFIG_ALREADY_EXISTS);
        ERROR_CODES.put("PENDING_EXTERNAL_BARCODE_IMPORT", ApiMgmtAccessControlErrorCode.PENDING_EXTERNAL_BARCODE_IMPORT);
    }

    @Autowired
    public MsAccessControlDatasource(@Value("${clients.services.ms-access-control}") String baseUrl,
                                     ObjectMapper jacksonMapper,
                                     TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
                .build();
    }

    public List<AccessControlSystem> getSystems(Long entityId, Long venueId) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("entityId", entityId);
        builder.addQueryParameter("venueId", venueId);
        return httpClient.buildRequest(HttpMethod.GET, SYSTEMS)
                .params(builder.build())
                .execute(ListType.of(AccessControlSystem.class));
    }

    public void createAccessControlSystemEntity(Long entityId, String accessControlSystem) {
        httpClient.buildRequest(HttpMethod.POST, ENTITY_ID)
                .pathParams(accessControlSystem, entityId)
                .execute();
    }

    public void deleteAccessControlSystemEntity(Long entityId, String accessControlSystem) {
        httpClient.buildRequest(HttpMethod.DELETE, ENTITY_ID)
                .pathParams(accessControlSystem, entityId)
                .execute();
    }

    public void createAccessControlSystemVenue(Long venueId, String accessControlSystem) {
        httpClient.buildRequest(HttpMethod.POST, VENUES)
                .pathParams(accessControlSystem)
                .body(new ClientRequestBody(new IdDTO(venueId)))
                .execute();
    }

    public void deleteAccessControlSystemVenue(Long venueId, String accessControlSystem) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_ID)
                .pathParams(accessControlSystem, venueId)
                .execute();
    }

    public SkidataVenueConfig getVenueSkidataConfig(Long venueId, String accessControlSystem) {
        return httpClient.buildRequest(HttpMethod.GET, VENUE_CONFIGURATION)
                .pathParams(accessControlSystem, venueId)
                .execute(SkidataVenueConfig.class);
    }

    public void createVenueSkidataConfig(Long venueId, String accessControlSystem, SkidataVenueConfig config) {
        httpClient.buildRequest(HttpMethod.POST, VENUE_CONFIGURATION)
                .pathParams(accessControlSystem, venueId)
                .body(new ClientRequestBody(config))
                .execute();
    }

    public void modifyVenueSkidataConfig(Long venueId, String accessControlSystem, SkidataVenueConfig config) {
        httpClient.buildRequest(HttpMethod.PUT, VENUE_CONFIGURATION)
                .pathParams(accessControlSystem, venueId)
                .body(new ClientRequestBody(config))
                .execute();
    }

    public void deleteVenueSkidataConfig(Long venueId, String accessControlSystem) {
        httpClient.buildRequest(HttpMethod.DELETE, VENUE_CONFIGURATION)
                .pathParams(accessControlSystem, venueId)
                .execute();
    }

    public void verifyImportAvailable(Long sessionId) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("sessionId", sessionId);
        httpClient.buildRequest(HttpMethod.GET, EXTERNAL_BARCODES_VERIFY)
                .params(builder.build())
                .execute();
    }

    public void startImport(StartImportProcessRequest startImportProcessRequest) {
        httpClient.buildRequest(HttpMethod.POST, EXTERNAL_BARCODES_START)
                .body(new ClientRequestBody(startImportProcessRequest))
                .execute();
    }

    public IdDTO getPendingImport(Long sessionId) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameter("sessionId", sessionId);
        return httpClient.buildRequest(HttpMethod.GET, EXTERNAL_BARCODES_PENDING)
                .params(builder.build())
                .execute(IdDTO.class);
    }

    public ExportProcess exportExternalBarcodes(ExternalBarcodesExportRequest body) {
        return httpClient.buildRequest(HttpMethod.POST, EXPORT_EXTERNAL_BARCODES)
                .body(new ClientRequestBody(body))
                .execute(ExportProcess.class);
    }

    public ExportProcess getExportExternalBarcodesStatus(String exportId, Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, EXPORT_EXTERNAL_BARCODES_STATUS)
                .pathParams(exportId, userId)
                .execute(ExportProcess.class);
    }

    public void addFortressSession(Long entityId, Long eventId, AddProductEventRequestDTO request) {
        httpClient.buildRequest(HttpMethod.POST, FORTRESS + ENTITY + EVENT + SESSIONS)
                .pathParams(entityId, eventId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void assignFortressSessionToSeasonTicket(Long entityId, Long seasonTicketId, HandlePackageEventRequestDTO request) {
        httpClient.buildRequest(HttpMethod.POST, FORTRESS_ST_ASSIGN)
                .pathParams(entityId, seasonTicketId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void unassignFortressSessionFromSeasonTicket(Long entityId, Long seasonTicketId, HandlePackageEventRequestDTO request) {
        httpClient.buildRequest(HttpMethod.POST, FORTRESS_ST_UNASSIGN)
                .pathParams(entityId, seasonTicketId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public ProductResponseDTO getFortressSeasonTicket(Long entityId, Long seasonTicketId) {
        return httpClient.buildRequest(HttpMethod.GET, FORTRESS + ENTITY + SEASON_TICKET)
                .pathParams(entityId, seasonTicketId)
                .execute(ProductResponseDTO.class);
    }

    public void createFortressSeasonTicket(Long entityId, Long seasonTicketId) {
        httpClient.buildRequest(HttpMethod.POST, FORTRESS + ENTITY + SEASON_TICKET)
                .pathParams(entityId, seasonTicketId)
                .execute();
    }

    public void addOrUpdateFortressRate(Long entityId, Long eventId, Long rateId) {
        httpClient.buildRequest(HttpMethod.POST, FORTRESS + ENTITY + EVENT + RATE)
                .pathParams(entityId, eventId, rateId)
                .execute();
    }

    public void addOrUpdateFortressVenueTemplate(Long entityId, Long venueTemplateId) {
        httpClient.buildRequest(HttpMethod.POST, FORTRESS + ENTITY + EVENT_VENUE_TEMPLATE_ID)
                .pathParams(entityId, venueTemplateId)
                .execute();
    }

}
