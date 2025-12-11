package es.onebox.mgmt.datasources.ms.collective;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.collectives.collectivecodes.dto.CollectiveCodeExportFileField;
import es.onebox.mgmt.datasources.ms.collective.dto.EntitiesCollective;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveCodesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectiveDetailDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsCollectivesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.MsExternalValidatorsDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveCodesSearchRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveCreateDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCollectiveUpdateDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCreateCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsCreateCollectiveCodesDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsDeleteCollectiveCodesBulkDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsEntitiesAssignationRequest;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveCodeDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveCodesBulkUnifiedDTO;
import es.onebox.mgmt.datasources.ms.collective.dto.request.MsUpdateCollectiveStatusRequest;
import es.onebox.mgmt.exception.ApiMgmtCollectivesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtExportsErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.export.dto.ExportFilter;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MsCollectiveDatasource {

    private static final int TIMEOUT = 60000;

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-collective-api/" + API_VERSION;

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();
    static {
        ERROR_CODES.put("COLLECTIVE_NOT_FOUND", ApiMgmtCollectivesErrorCode.COLLECTIVE_NOT_FOUND);
        ERROR_CODES.put("COLLECTIVE_CODE_INVALID", ApiMgmtCollectivesErrorCode.COLLECTIVE_CODE_INVALID);
        ERROR_CODES.put("MSC0005", ApiMgmtCollectivesErrorCode.COLLECTIVE_NO_SUCH_MANDATORY_FIELDS);
        ERROR_CODES.put("ENTITIES_ASSIGNED_TO_COLLECTIVE", ApiMgmtCollectivesErrorCode.COLLECTIVE_ENTITIES_ASSIGNED_TO_COLLECTIVE);
        ERROR_CODES.put("ACTIVE_PROMOTION_TEMPLATES_TO_COLLECTIVE", ApiMgmtCollectivesErrorCode.COLLECTIVE_ACTIVE_PROMOTION_TEMPLATES_TO_COLLECTIVE);
        ERROR_CODES.put("BAD_REQUEST_PARAMETER", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("INVALID_TYPE_VALIDATION_METHOD_SET", ApiMgmtCollectivesErrorCode.COLLECTIVE_INVALID_TYPE_VALIDATION_METHOD_SET);
        ERROR_CODES.put("EXTERNAL_VALIDATOR_MANDATORY", ApiMgmtCollectivesErrorCode.COLLECTIVE_EXTERNAL_VALIDATOR_MANDATORY);
        ERROR_CODES.put("EXTERNAL_VALIDATOR_NOT_FOUND", ApiMgmtCollectivesErrorCode.COLLECTIVE_EXTERNAL_VALIDATOR_NOT_FOUND);
        ERROR_CODES.put("EXTERNAL_TYPE_NOT_APPLICABLE", ApiMgmtCollectivesErrorCode.COLLECTIVE_EXTERNAL_TYPE_NOT_APPLICABLE);
        ERROR_CODES.put("MSC0002", ApiMgmtCollectivesErrorCode.COLLECTIVE_CODE_NOT_FOUND);
        ERROR_CODES.put("COLLECTIVE_CODE_INVALID_VALIDITY_PERIOD_DATES", ApiMgmtCollectivesErrorCode.COLLECTIVE_CODE_INVALID_VALIDITY_PERIOD_DATES);
        ERROR_CODES.put("COLLECTIVE_CODE_PASSWORD_MANDATORY_FOR_USER_PASSWORD_VALIDATION", ApiMgmtCollectivesErrorCode.COLLECTIVE_CODE_KEY_MANDATORY_FOR_USER_PASSWORD_VALIDATION);
        ERROR_CODES.put("COLLECTIVE_CODE_INVALID_USAGE_LIMIT_FOR_GIFT_TICKET", ApiMgmtCollectivesErrorCode.COLLECTIVE_CODE_INVALID_USAGE_LIMIT_FOR_GIFT_TICKET);
        ERROR_CODES.put("COLLECTIVE_CODE_BULK_UPDATE_AT_LEAST_ONE_DATA_FIELD_MANDATORY", ApiMgmtCollectivesErrorCode.COLLECTIVE_CODE_BULK_UPDATE_AT_LEAST_ONE_DATA_FIELD_MANDATORY);
        ERROR_CODES.put("EXPORT_LIMIT_REACHED", ApiMgmtExportsErrorCode.EXPORT_LIMIT_REACHED);
        ERROR_CODES.put("EXPORT_STATUS_NOT_FOUND", ApiMgmtExportsErrorCode.EXPORT_NOT_FOUND);

        ERROR_CODES.put("EXPORT_WITH_TOO_MANY_RECORDS", ApiMgmtExportsErrorCode.EXPORT_WITH_TOO_MANY_RECORDS);
        ERROR_CODES.put("MSC0003", ApiMgmtCollectivesErrorCode.COLLECTIVE_CODE_ALREADY_USED);
        ERROR_CODES.put("SHOW_USAGES_ONLY_COLLECTIVE_INTERNAL", ApiMgmtCollectivesErrorCode.SHOW_USAGES_ONLY_COLLECTIVE_INTERNAL);
        ERROR_CODES.put("SHOW_USAGES_GIFT_TICKET_NOT_COMPATIBLE", ApiMgmtCollectivesErrorCode.SHOW_USAGES_GIFT_TICKET_NOT_COMPATIBLE);

    }

    @Autowired
    public MsCollectiveDatasource(@Value("${clients.services.ms-collective}") String baseUrl,
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

    public MsCollectivesDTO getCollectives(MsCollectiveRequest request){
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, "/collectives")
                .params(params)
                .execute(MsCollectivesDTO.class);
    }

    public MsCollectiveDetailDTO getCollective(Long collectiveId, Long operatorId, Long entityId, Long entityAdminId) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameter("operatorId", operatorId).
                addQueryParameter("entityId", entityId).
                addQueryParameter("entityAdminId", entityAdminId).
                build();
        return httpClient.buildRequest(HttpMethod.GET, "/collectives/{id}")
                .pathParams(collectiveId)
                .params(params)
                .execute(MsCollectiveDetailDTO.class);
    }

    public IdDTO createCollective(MsCollectiveCreateDTO request) {
        return httpClient.buildRequest(HttpMethod.POST, "/collectives")
                .body(new ClientRequestBody(request))
                .execute(IdDTO.class);
    }

    public void updateCollective(Long collectiveId, MsCollectiveUpdateDTO msCollectiveUpdateDTO) {
        httpClient.buildRequest(HttpMethod.PUT, "/collectives/{id}")
                .pathParams(collectiveId)
                .body(new ClientRequestBody(msCollectiveUpdateDTO))
                .execute();
    }

    public void deleteCollective(Long collectiveId) {
        httpClient.buildRequest(HttpMethod.DELETE, "/collectives/{id}")
                .pathParams(collectiveId)
                .execute();
    }

    public void updateCollectiveStatus(Long collectiveId, MsUpdateCollectiveStatusRequest request) {
        httpClient.buildRequest(HttpMethod.PUT, "/collectives/{id}/status")
                .pathParams(collectiveId)
                .body(new ClientRequestBody(request))
                .execute();
    }


    public MsExternalValidatorsDTO getExternalValidators(){
        return httpClient.buildRequest(HttpMethod.GET, "/external-validators")
                .execute(MsExternalValidatorsDTO.class);
    }

    public EntitiesCollective getEntitiesAssignedToCollective(Long collectiveId){
        return httpClient.buildRequest(HttpMethod.GET, "/collectives/{id}/entities")
                .pathParams(collectiveId)
                .execute(EntitiesCollective.class);
    }

    public void assignEntitiesToCollective(Long collectiveId, MsEntitiesAssignationRequest request){
        httpClient.buildRequest(HttpMethod.PUT, "/collectives/{id}/entities")
                .pathParams(collectiveId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public MsCollectiveCodesDTO getCollectiveCodes(Long collectiveId, MsCollectiveCodesSearchRequest request){
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(request)
                .build();
        return httpClient.buildRequest(HttpMethod.GET, "/collectives/{collectiveId}/codes")
                .pathParams(collectiveId)
                .params(params)
                .execute(MsCollectiveCodesDTO.class);
    }

    public MsCollectiveCodeDTO getCollectiveCode(Long collectiveId, String code){
        return httpClient.buildRequest(HttpMethod.GET, "/collectives/{collectiveId}/codes/{code}")
                .pathParams(collectiveId, code)
                .execute(MsCollectiveCodeDTO.class);
    }

    public void createCollectiveCode(Long collectiveId, MsCreateCollectiveCodeDTO request){
        httpClient.buildRequest(HttpMethod.POST, "/collectives/{collectiveId}/codes")
                .pathParams(collectiveId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void createCollectiveCodes(Long collectiveId, MsCreateCollectiveCodesDTO request){
        httpClient.buildRequest(HttpMethod.POST, "/collectives/{collectiveId}/codes/bulk")
                .pathParams(collectiveId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void updateCollectiveCode(Long collectiveId, String code, MsUpdateCollectiveCodeDTO request) {
        httpClient.buildRequest(HttpMethod.PUT, "/collectives/{collectiveId}/codes/{code}")
                .pathParams(collectiveId, code)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void updateCollectiveCodes(Long collectiveId,
                                      MsCollectiveCodesSearchRequest filter,
                                      MsUpdateCollectiveCodesBulkUnifiedDTO request) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(filter)
                .build();
        httpClient.buildRequest(HttpMethod.PUT, "/collectives/{collectiveId}/codes/bulk-unified")
                .pathParams(collectiveId)
                .params(params)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteCollectiveCode(Long collectiveId, String code) {
        httpClient.buildRequest(HttpMethod.DELETE, "/collectives/{collectiveId}/codes/{code}")
                .pathParams(collectiveId, code)
                .execute();
    }

    public void deleteCollectiveCodes(Long collectiveId,
                                      MsCollectiveCodesSearchRequest filter,
                                      MsDeleteCollectiveCodesBulkDTO request) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameters(filter)
                .build();
        httpClient.buildRequest(HttpMethod.POST, "/collectives/{collectiveId}/codes/bulk-delete")
                .pathParams(collectiveId)
                .params(params)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public ExportProcess generateCollectiveCodesReport(Long collectiveId, ExportFilter<CollectiveCodeExportFileField> filter) {
        return httpClient.buildRequest(HttpMethod.POST, "/collectives/{collectiveId}/codes/exports")
                .pathParams(collectiveId)
                .body(new ClientRequestBody(filter))
                .execute(ExportProcess.class);
    }

    public ExportProcess getCollectiveCodesReportStatus(Long collectiveId, String exportId, Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, "/collectives/{collectiveId}/codes/exports/{exportId}/users/{userId}/status")
                .pathParams(collectiveId, exportId, userId)
                .execute(ExportProcess.class);
    }
}
