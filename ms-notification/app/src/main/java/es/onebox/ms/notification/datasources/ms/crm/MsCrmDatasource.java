package es.onebox.ms.notification.datasources.ms.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmClientResponse;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmOrderResponse;
import es.onebox.ms.notification.datasources.ms.crm.dto.CrmParams;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MsCrmDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-crm-api/" + API_VERSION;

    private static final String ABANDONED_STATUS = "ABANDONED";

    private final HttpClient httpClient;

    @Autowired
    public MsCrmDatasource(@Value("${clients.services.ms-crm}") String baseUrl,
                           ObjectMapper jacksonMapper,
                           TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .build();
    }

    public CrmOrderResponse getAuditCrmOrders(String orderId, Long entityId) {
        CrmParams filter = new CrmParams();
        filter.setId(orderId);
        filter.setStatus(ABANDONED_STATUS);
        filter.setClient_id(entityId);
        return httpClient.buildRequest(HttpMethod.POST, "/auditcrm/orders")
                .body(new ClientRequestBody(filter))
                .execute(CrmOrderResponse.class);
    }

    public CrmClientResponse getAuditCrmBuyers(String email, Long entityId) {
        CrmParams filter = new CrmParams();
        filter.setId(email);
        filter.setStatus(ABANDONED_STATUS);
        filter.setClient_id(entityId);
        return httpClient.buildRequest(HttpMethod.POST, "/auditcrm/buyers")
                .body(new ClientRequestBody(filter))
                .execute(CrmClientResponse.class);
    }

}
