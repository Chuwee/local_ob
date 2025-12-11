package es.onebox.mgmt.datasources.api.accounting;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.core.file.exporter.status.model.ExportProcess;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.datasources.api.accounting.dto.BalanceRequest;
import es.onebox.mgmt.datasources.api.accounting.dto.ClientTransactionsExportFilter;
import es.onebox.mgmt.datasources.api.accounting.dto.DepositRequest;
import es.onebox.mgmt.datasources.api.accounting.dto.ProviderClient;
import es.onebox.mgmt.datasources.api.accounting.dto.SearchTransactionsFilter;
import es.onebox.mgmt.datasources.api.accounting.dto.TransactionAudits;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelAccounting;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtExportsErrorCode;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiAccountingDatasource {

    private static final int TIMEOUT = 60000;

    private static final String ACCOUNTING_API_VERSION = "1.0";
    private static final String ACCOUNTING_API_PATH = "/accounting-api/" + ACCOUNTING_API_VERSION;

    private static final String AUDITORIES = ACCOUNTING_API_PATH + "/auditories";
    private static final String PROVIDERS = ACCOUNTING_API_PATH + "/providers";
    private static final String PROVIDER_CHANNEL = PROVIDERS + "/{providerId}/channels/{channelId}";
    private static final String ACCOUNTING = ACCOUNTING_API_PATH + "/accounting";
    private static final String BALANCE = PROVIDERS + "/{providerId}/clients/{clientId}";
    private static final String MODIFY_AMOUNT = ACCOUNTING + "/modifyAmount";
    private static final String DEPOSIT = ACCOUNTING + "/deposit";
    private static final String MAX_CREDIT = ACCOUNTING + "/maxCredit";
    private static final String EXPORT = "/transactions/export";
    private static final String EXPORT_STATUS = EXPORT + "/{exportId}/users/{userId}/status";

    private final HttpClient httpClient;

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("BAD_REQUEST_PARAMETER", ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("EXPORT_LIMIT_REACHED", ApiMgmtExportsErrorCode.EXPORT_LIMIT_REACHED);
        ERROR_CODES.put("EXPORT_WITH_TOO_MANY_RECORDS", ApiMgmtExportsErrorCode.EXPORT_WITH_TOO_MANY_RECORDS);
        ERROR_CODES.put("EXPORT_NOT_FOUND", ApiMgmtExportsErrorCode.EXPORT_NOT_FOUND);
        ERROR_CODES.put("CURRENCY_NOT_FOUND", ApiMgmtExportsErrorCode.CURRENCY_NOT_FOUND);
        ERROR_CODES.put("CURRENCIES_NOT_EQUALS", ApiMgmtExportsErrorCode.CURRENCIES_NOT_EQUALS);
        ERROR_CODES.put("404G0001", ApiMgmtErrorCode.NO_ACCOUNT);
        ERROR_CODES.put("400G0001", ApiMgmtErrorCode.INACTIVE_ACCOUNT);
    }

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

    public ProviderClient getBalance(Long entityId, Long clientId) {
        return httpClient.buildRequest(HttpMethod.GET, BALANCE)
                .pathParams(entityId, clientId)
                .execute(ProviderClient.class);
    }

    public TransactionAudits getTransactions(SearchTransactionsFilter filter) {
        QueryParameters.Builder builder = new QueryParameters.Builder();
        builder.addQueryParameters(filter);
        return httpClient.buildRequest(HttpMethod.GET, AUDITORIES)
                .params(builder.build())
                .execute(TransactionAudits.class);
    }

    public void deposit(DepositRequest request) {
        httpClient.buildRequest(HttpMethod.POST, DEPOSIT)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void cashAdjustment(BalanceRequest request) {
        httpClient.buildRequest(HttpMethod.POST, MODIFY_AMOUNT)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void editCreditLimit(BalanceRequest request) {
        httpClient.buildRequest(HttpMethod.POST, MAX_CREDIT)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public ExportProcess exportTransactions(ClientTransactionsExportFilter filter) {
        return httpClient.buildRequest(HttpMethod.POST, EXPORT)
                .body(new ClientRequestBody(filter))
                .execute(ExportProcess.class);
    }

    public ExportProcess exportTransactionsStatus(String exportId, Long userId) {
        return httpClient.buildRequest(HttpMethod.GET, EXPORT_STATUS)
                .pathParams(exportId, userId)
                .execute(ExportProcess.class);
    }

    public void createProviderClientAssociation(Long entityId, Long clientId) {
        httpClient.buildRequest(HttpMethod.PUT, BALANCE)
                .pathParams(entityId, clientId)
                .execute();
    }

    public void deactivateProviderClientAssociation(Long entityId, Long clientId) {
        httpClient.buildRequest(HttpMethod.DELETE, BALANCE)
                .pathParams(entityId, clientId)
                .execute();
    }

    public ChannelAccounting upsertProviderChannelAccounting(Long providerId, Long channelId) {
        return httpClient.buildRequest(HttpMethod.PUT, PROVIDER_CHANNEL)
                .pathParams(providerId, channelId)
                .execute(ChannelAccounting.class);
    }
}
