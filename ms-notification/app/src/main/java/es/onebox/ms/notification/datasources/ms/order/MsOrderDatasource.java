package es.onebox.ms.notification.datasources.ms.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.exception.ErrorCode;
import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.ms.notification.common.dto.MemberOrderDTO;
import es.onebox.ms.notification.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MsOrderDatasource {

    private static final Map<String, ErrorCode> ERROR_CODES = new HashMap<>();

    private static final String API_VERSION = "v1";
    private static final String BASE_URL = "/orders-api/" + API_VERSION;

    private static final String ORDERS = "/orders";
    private static final String MEMBER_ORDERS = BASE_URL + "/member-orders";
    private static final String ORDER_CODE = "/{orderCode}";

    private final HttpClient httpClient;

    @Autowired
    public MsOrderDatasource(@Value("${clients.services.ms-order}") String baseUrl,
                             ObjectMapper jacksonMapper,
                             TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder(ERROR_CODES, jacksonMapper))
                .build();
    }

    public OrderDTO getOrderByCode(String code) {
        return httpClient.buildRequest(HttpMethod.GET, ORDERS + ORDER_CODE)
                .pathParams(code)
                .execute(OrderDTO.class);
    }

    public MemberOrderDTO getCouchbaseMemberOrderByCode(String code) {
        return httpClient.buildRequest(HttpMethod.GET, MEMBER_ORDERS + ORDER_CODE + "/couchbase")
                .pathParams(code)
                .execute(MemberOrderDTO.class);
    }

}
