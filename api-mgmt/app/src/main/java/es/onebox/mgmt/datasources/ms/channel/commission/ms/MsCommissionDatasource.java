package es.onebox.mgmt.datasources.ms.channel.commission.ms;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.channels.commissions.dto.CommissionTypeDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCommission;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MsCommissionDatasource {
    private static final int TIMEOUT = 60000;

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-channel-api/" + API_VERSION + "/channels";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("CHANNEL_NOT_FOUND", ApiMgmtErrorCode.CHANNEL_NOT_FOUND);
        ERROR_CODES.put("CHANNEL_ID_MANDATORY", ApiMgmtChannelsErrorCode.CHANNEL_ID_INVALID);
        ERROR_CODES.put("AT_LEAST_ONE_RANGE", ApiMgmtErrorCode.AT_LEAST_ONE_RANGE);
        ERROR_CODES.put("COMMISSION_FROM_RANGE_MANDATORY", ApiMgmtErrorCode.COMMISSION_FROM_RANGE_MANDATORY);
        ERROR_CODES.put("COMMISSION_FROM_RANGE_DUPLICATED", ApiMgmtErrorCode.COMMISSION_FROM_RANGE_DUPLICATED);
        ERROR_CODES.put("FROM_RANGE_ZERO_MANDATORY", ApiMgmtErrorCode.COMMISSION_FROM_RANGE_ZERO_MANDANTORY);
        ERROR_CODES.put("MIN_COMMISSION_GREATER_THAN_MAX", ApiMgmtErrorCode.MIN_COMMISSION_GREATER_THAN_MAX);
        ERROR_CODES.put("FIXED_OR_PERCENTAGE_MANDATORY", ApiMgmtErrorCode.FIXED_OR_PERCENTAGE_MANDATORY);
        ERROR_CODES.put("COMMISSION_TYPE_NOT_SUPPORTED", ApiMgmtErrorCode.COMMISSION_TYPE_NOT_SUPPORTED);
        ERROR_CODES.put("COMMISSION_TYPE_DUPLICATED", ApiMgmtErrorCode.COMMISSION_TYPE_DUPLICATED);
        ERROR_CODES.put("NEGATIVE_VALUE", ApiMgmtErrorCode.NEGATIVE_VALUE);
    }

    @Autowired
    public MsCommissionDatasource(@Value("${clients.services.ms-channel}") String baseUrl,
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


    public void setCommission(long channelId, List<ChannelCommission> msChannelCommissionRequestDTO) {
        httpClient.buildRequest(HttpMethod.POST, "/{channelId}/commissions")
                .pathParams(channelId)
                .body(new ClientRequestBody(msChannelCommissionRequestDTO))
                .execute();
    }

    public List<ChannelCommission> getChannelCommissions(Long channelId, List<CommissionTypeDTO> types, List<Integer> currencyIds) {
        QueryParameters.Builder params = new QueryParameters.Builder();
        if (!CommonUtils.isEmpty(types)) {
            for (CommissionTypeDTO commissionType : types) {
                params.addQueryParameter("type", commissionType.toString());
            }
        }
        if (!CommonUtils.isEmpty(currencyIds)) {
            currencyIds.forEach(id -> params.addQueryParameter("currencyId", id));
        }
        return httpClient.buildRequest(HttpMethod.GET, "/{channelId}/commissions")
                .pathParams(channelId)
                .params(params.build())
                .execute(ListType.of(ChannelCommission.class));
    }
}
