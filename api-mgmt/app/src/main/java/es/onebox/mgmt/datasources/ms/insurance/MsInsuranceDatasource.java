package es.onebox.mgmt.datasources.ms.insurance;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.mgmt.datasources.ms.insurance.dto.SearchInsurerFilter;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.datasources.ms.insurance.dto.ChannelCancellationServices;
import es.onebox.mgmt.datasources.ms.insurance.dto.ChannelCancellationServicesUpdate;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicies;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsurancePolicyV1;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsuranceRange;
import es.onebox.mgmt.datasources.ms.insurance.dto.InsuranceTermsConditionsList;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurer;
import es.onebox.mgmt.datasources.ms.insurance.dto.Insurers;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsurancePolicy;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsuranceTermsConditions;
import es.onebox.mgmt.datasources.ms.insurance.dto.UpdateInsuranceTermsConditionsFileContent;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtInsuranceErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MsInsuranceDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-insurance/" + API_VERSION;
    private static final String INSURERS = "/insurers";
    private static final String INSURER_ID = "/{insurerId}";
    private static final String INSURANCE_POLICY_ID = "/{policyId}";
    private static final String INSURANCE_POLICIES = INSURERS + INSURER_ID + "/policies";
    private static final String INSURANCE_POLICY_ID_PATH = INSURANCE_POLICIES + INSURANCE_POLICY_ID;
    private static final String INSURANCE_RANGES = INSURANCE_POLICY_ID_PATH + "/ranges";
    private static final String INSURANCE_TERMS_CONDITIONS = INSURANCE_POLICY_ID_PATH + "/terms-conditions";
    private static final String INSURANCE_TERMS_CONDITIONS_ID = "/{termsId}";
    private static final String INSURANCE_TERMS_CONDITIONS_FILE_CONTENT = INSURANCE_TERMS_CONDITIONS + INSURANCE_TERMS_CONDITIONS_ID + "/file-content";
    private static final int TIMEOUT = 60000;

    private static final String CHANNELS = "/channels";
    private static final String CHANNEL_ID = "/{channelId}";
    private static final String CANCELLATION_SERVICES = "/cancellation-services";


    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("500C0002", ApiMgmtErrorCode.GENERIC_ERROR);
        ERROR_CODES.put("INSURANCE_POLICY_NOT_ACCESSIBLE", ApiMgmtInsuranceErrorCode.INSURANCE_POLICY_NOT_ACCESSIBLE);
        ERROR_CODES.put("INSURANCE_POLICY_IDS_IS_NOT_NULL", ApiMgmtInsuranceErrorCode.INSURANCE_POLICY_IDS_IS_NOT_NULL);
        ERROR_CODES.put("INSURANCE_POLICES_NOT_ALLOWED_IN_CHANNEL", ApiMgmtInsuranceErrorCode.INSURANCE_POLICES_NOT_ALLOWED_IN_CHANNEL);
        ERROR_CODES.put("INSURANCE_POLICY_IDS_IS_NULL", ApiMgmtInsuranceErrorCode.INSURANCE_POLICY_IDS_IS_NULL);
        ERROR_CODES.put("CHANNEL_NOT_FOUND", ApiMgmtInsuranceErrorCode.CHANNEL_NOT_FOUND);
        ERROR_CODES.put("INSURANCE_POLICES_NOT_AVAILABLE_FOR_CHANNEL", ApiMgmtInsuranceErrorCode.INSURANCE_POLICES_NOT_AVAILABLE_FOR_CHANNEL);
        ERROR_CODES.put("INSURER_NOT_FOUND", ApiMgmtInsuranceErrorCode.INSURER_NOT_FOUND);
        ERROR_CODES.put("INSURANCE_POLICY_NOT_FOUND", ApiMgmtInsuranceErrorCode.INSURANCE_POLICY_NOT_FOUND);
        ERROR_CODES.put("INSURANCE_POLICY_NOT_IN_INSURER", ApiMgmtInsuranceErrorCode.INSURANCE_POLICY_NOT_IN_INSURER);
        ERROR_CODES.put("TERMS_CONDITIONS_NOT_FOUND", ApiMgmtInsuranceErrorCode.TERMS_CONDITIONS_NOT_FOUND);
        ERROR_CODES.put("INVALID_FILE_CONTENT", ApiMgmtInsuranceErrorCode.INVALID_FILE_CONTENT);
        ERROR_CODES.put("INVALID_FILE_NAME", ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME);
        ERROR_CODES.put("INVALID_FILE_NAME_SPACES", ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME_SPACES);
        ERROR_CODES.put("INVALID_FILE_NAME_CHARACTERS", ApiMgmtInsuranceErrorCode.INVALID_FILE_NAME_CHARACTERS);
        ERROR_CODES.put("TERMS_CONDITIONS_FILE_CONTENT_NOT_FOUND", ApiMgmtInsuranceErrorCode.TERMS_CONDITIONS_FILE_CONTENT_NOT_FOUND);
        ERROR_CODES.put("BAD_REQUEST_PARAMETER", ApiMgmtInsuranceErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("CONFLICT_INSURANCE_CHANNEL_PARAMETER", ApiMgmtInsuranceErrorCode.CONFLICT_INSURANCE_CHANNEL_PARAMETER);
    }

    public static ErrorCode getErrorCode(String msEventErrorCode) {
        return ERROR_CODES.getOrDefault(msEventErrorCode, ApiMgmtErrorCode.GENERIC_ERROR);
    }

    @Autowired
    public MsInsuranceDatasource(@Value("${clients.services.ms-insurance}") String baseUrl,
                                 ObjectMapper jacksonMapper,
                                 TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .readTimeout(TIMEOUT)
                .build();
    }

    public void updateChannelCancellationServices(Long channelId, ChannelCancellationServicesUpdate cancellationServices) {
        httpClient.buildRequest(HttpMethod.PUT, BASE_PATH + CHANNELS + CHANNEL_ID + CANCELLATION_SERVICES)
                .pathParams(channelId)
                .body(new ClientRequestBody(cancellationServices))
                .execute();
    }

    public ChannelCancellationServices getChannelCancellationServices(Long channelId, Long userOperatorId) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameter("operatorId", userOperatorId).
                build();
        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + CHANNELS + CHANNEL_ID + CANCELLATION_SERVICES)
                .pathParams(channelId)
                .params(params)
                .execute(ChannelCancellationServices.class);
    }

    public Insurer getInsurer(Integer insurerId) {
        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + INSURERS + INSURER_ID)
                .pathParams(insurerId)
                .execute(Insurer.class);
    }

    public Insurer createInsurer(Insurer insurer) {
        return httpClient.buildRequest(HttpMethod.POST, BASE_PATH + INSURERS)
                .body(new ClientRequestBody(insurer))
                .execute(Insurer.class);
    }

    public Insurer updateInsurer(Insurer insurer) {
        return httpClient.buildRequest(HttpMethod.PUT, BASE_PATH + INSURERS + INSURER_ID)
                .pathParams(insurer.getId())
                .body(new ClientRequestBody(insurer))
                .execute(Insurer.class);
    }

    public Insurers searchInsurers(SearchInsurerFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + INSURERS)
                .params(builder.build())
                .execute(Insurers.class);
    }

    public InsurancePolicies getPoliciesByInsurerId(Integer insurerId) {
        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + INSURANCE_POLICIES)
                .pathParams(insurerId)
                .execute(InsurancePolicies.class);
    }

    public InsuranceTermsConditionsList getTermsConditionsByPolicyId(Integer insurerId, Integer policyId, String lang) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        if (lang != null) {
            builder.addQueryParameter("lang", lang);
        }

        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + INSURANCE_TERMS_CONDITIONS)
                .pathParams(insurerId, policyId)
                .params(builder.build())
                .execute(InsuranceTermsConditionsList.class);
    }

    public void updateTermsConditionsById(Integer insurerId, Integer policyId, Integer termsId, UpdateInsuranceTermsConditions updateInsuranceTermsConditions) {
        httpClient.buildRequest(HttpMethod.PUT, BASE_PATH + INSURANCE_TERMS_CONDITIONS + INSURANCE_TERMS_CONDITIONS_ID)
                .pathParams(insurerId, policyId, termsId)
                .body(new ClientRequestBody(updateInsuranceTermsConditions))
                .execute();
    }


    public String getTermsConditionsFileContent(Integer insurerId, Integer policyId, Integer termsId) {
        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + INSURANCE_TERMS_CONDITIONS_FILE_CONTENT)
                .pathParams(insurerId, policyId, termsId)
                .execute(String.class);
    }

    public void updateTermsConditionsFileContent(Integer insurerId, Integer policyId, Integer termsId, UpdateInsuranceTermsConditionsFileContent newFileContent) {
        httpClient.buildRequest(HttpMethod.PUT, BASE_PATH + INSURANCE_TERMS_CONDITIONS_FILE_CONTENT)
                .pathParams(insurerId, policyId, termsId)
                .body(new ClientRequestBody(newFileContent))
                .execute();
    }

    public InsurancePolicyV1 getPolicyDetails(Integer insurerId, Integer policyId) {
        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + INSURANCE_POLICY_ID_PATH)
                .pathParams(insurerId, policyId)
                .execute(InsurancePolicyV1.class);
    }

    public List<InsuranceRange> getRangesByPolicyId(Integer insurerId, Integer policyId) {
        return httpClient.buildRequest(HttpMethod.GET, BASE_PATH + INSURANCE_RANGES)
                .pathParams(insurerId, policyId)
                .execute(ListType.of(InsuranceRange.class));
    }

    public List<InsuranceRange> updateRangesByPolicyId(
            Integer insurerId, Integer policyId, List<InsuranceRange> insuranceRangeList) {
        return httpClient.buildRequest(HttpMethod.POST, BASE_PATH + INSURANCE_RANGES)
                .pathParams(insurerId, policyId)
                .body(new ClientRequestBody(insuranceRangeList))
                .execute(ListType.of(InsuranceRange.class));
    }

    public InsurancePolicyV1 createPolicy(Integer insurerId, InsurancePolicyV1 insurancePolicy) {
        return httpClient.buildRequest(HttpMethod.POST, BASE_PATH + INSURANCE_POLICIES)
                .pathParams(insurerId)
                .body(new ClientRequestBody(insurancePolicy))
                .execute(InsurancePolicyV1.class);
    }

    public void updatePolicy(Integer insurerId, Integer policyId, UpdateInsurancePolicy updateInsurancePolicy) {
        httpClient.buildRequest(HttpMethod.PUT, BASE_PATH + INSURANCE_POLICY_ID_PATH)
                .pathParams(insurerId, policyId)
                .body(new ClientRequestBody(updateInsurancePolicy))
                .execute();
    }

    public void deletePolicy(Integer insurerId, Integer policyId) {
        httpClient.buildRequest(HttpMethod.DELETE, BASE_PATH + INSURANCE_POLICY_ID_PATH)
                .pathParams(insurerId, policyId)
                .execute();
    }
}
