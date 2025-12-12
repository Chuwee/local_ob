package es.onebox.common.datasources.accounting;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.accounting.dto.TransactionAudit;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.exception.ClientHttpExceptionBuilder;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiAccountingDatasource {

    private static final int TIMEOUT = 60000;

    private static final String ACCOUNTING_API_VERSION = "1.0";
    private static final String ACCOUNTING_API_PATH = "/accounting-api/" + ACCOUNTING_API_VERSION;

    private static final String AUDITORIES = ACCOUNTING_API_PATH + "/auditories";
    private static final String AUDITORIES_TRANSACTION_ID = AUDITORIES + "/{transactionId}";

    private final HttpClient httpClient;

    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    @Autowired
    public ApiAccountingDatasource(@Value("${clients.services.api-accounting}") String baseUrl,
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

    public List<TransactionAudit> getTransaction(String movementId) {
        return httpClient.buildRequest(HttpMethod.GET, AUDITORIES_TRANSACTION_ID)
                .pathParams(movementId)
                .execute(ListType.of(TransactionAudit.class));
    }

}
