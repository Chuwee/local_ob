package es.onebox.common.datasources.distribution;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.distribution.dto.AddCollectiveRequest;
import es.onebox.common.datasources.distribution.dto.AddPromotionRequest;
import es.onebox.common.datasources.distribution.dto.AddSeatsDTO;
import es.onebox.common.datasources.distribution.dto.InvitationRequest;
import es.onebox.common.datasources.distribution.dto.OrderResponse;
import es.onebox.common.datasources.distribution.dto.PresalesRequest;
import es.onebox.common.datasources.distribution.dto.PromotionCollectiveRequest;
import es.onebox.common.datasources.distribution.dto.PromotionRequestType;
import es.onebox.common.datasources.distribution.dto.RenewalSeats;
import es.onebox.common.datasources.distribution.dto.SeatsAutoRequest;
import es.onebox.common.datasources.distribution.dto.attendee.ItemAttendees;
import es.onebox.common.datasources.distribution.dto.deliverymethods.DeliveryMethodsRequestDTO;
import es.onebox.common.datasources.distribution.dto.deliverymethods.PreConfirmRequestDTO;
import es.onebox.common.datasources.distribution.dto.order.ConfirmRequest;
import es.onebox.common.exception.ApiExceptionBuilder;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.HttpResponse;
import es.onebox.datasource.http.response.HttpResponseBodyType;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ApiDistributionDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/distribution-api/" + API_VERSION;

    private static final long CONNECTION_TIMEOUT = 10000L;
    private static final long READ_TIMEOUT = 60000L;

    private final HttpClient httpClient;

    private final String ORDER_ID_HEADER_NAME = "ob-order-id";
    public static final String SESSION_PREVIEW_TOKEN_HEADER = "ob-session-preview-token";
    private final String ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String CONTENT_TYPE_HEADER = "Content-Type";
    private final String OB_LANGUAGE = "ob-language";


    private static final Map<String, ApiExternalErrorCode> ERROR_CODES = new HashMap<>();

    static {
        ERROR_CODES.put("ORDER_INVALID_PROMOTION", ApiExternalErrorCode.ORDER_INVALID_PROMOTION);
        ERROR_CODES.put("PARAMETER_REQUIRED", ApiExternalErrorCode.PARAMETER_REQUIRED);
        ERROR_CODES.put("ORDER_INVALID_COLLECTIVE", ApiExternalErrorCode.ORDER_INVALID_COLLECTIVE);
        ERROR_CODES.put("DIST0000", ApiExternalErrorCode.GENERIC_REST_EXCEPTION);
        ERROR_CODES.put("ORDER_SEAT_NOT_AVAILABLE", ApiExternalErrorCode.ORDER_SEAT_NOT_AVAILABLE);
        ERROR_CODES.put("ORDER_ATTENDEE_DATA_REQUIRED", ApiExternalErrorCode.ORDER_SEAT_NOT_AVAILABLE);
        ERROR_CODES.put("ORDER_NOT_FOUND", ApiExternalErrorCode.ORDER_NOT_FOUND);
        ERROR_CODES.put("DIST0001", ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        ERROR_CODES.put("ORDER_ITEMS_LIMIT_EXCEEDED", ApiExternalErrorCode.ORDER_ITEMS_LIMIT_EXCEEDED);
        ERROR_CODES.put("SESSION_NOT_FOUND", ApiExternalErrorCode.SESSION_NOT_FOUND);
        ERROR_CODES.put("ORDER_ITEMS_NOT_SAME_PRICE_TYPES", ApiExternalErrorCode.ORDER_ITEMS_NOT_SAME_PRICE_TYPES);
        ERROR_CODES.put("PRESALE_NOT_FOUND", ApiExternalErrorCode.PRESALE_NOT_FOUND);
        ERROR_CODES.put("ORDER_ATTENDEE_DATA_INVALID_FORMAT", ApiExternalErrorCode.ORDER_ATTENDEE_DATA_INVALID_FORMAT);
    }

    private final ObjectMapper jacksonMapper;

    @Autowired
    public ApiDistributionDatasource(@Value("${clients.services.api-distribution}") String baseUrl,
                                     ObjectMapper jacksonMapper,
                                     TracingInterceptor tracingInterceptor) {
        ObjectMapper mapper = JsonMapper.jacksonMapper();
        this.httpClient = HttpClientFactoryBuilder.builder()
                .connectTimeout(CONNECTION_TIMEOUT)
                .readTimeout(READ_TIMEOUT)
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(mapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ApiExceptionBuilder(ERROR_CODES, mapper))
                .build();
        this.jacksonMapper = jacksonMapper;
    }

    public void addPromotion(String channelOauthToken, String cartToken, Long promotionId, List<Long> itemIds,
                             PromotionRequestType type, String pin, String code, String discountType, Double discountValue,
                             String sessionPreviewToken) {
        RequestHeaders headers = new RequestHeaders.Builder()
                .addHeader("Authorization", "Bearer " + channelOauthToken)
                .addHeader("ob-order-id", cartToken)
                .build();
        if (sessionPreviewToken != null) {
            headers.getHeaders().put("Session-Preview-Token", sessionPreviewToken);
        }
        AddPromotionRequest addPromotionRequest = new AddPromotionRequest();
        addPromotionRequest.setId(promotionId);
        addPromotionRequest.setType(type);
        if (PromotionRequestType.ORDER.equals(type)) {
            PromotionCollectiveRequest promotionCollectiveRequest =
                    new PromotionCollectiveRequest(code, pin);
            addPromotionRequest.setCollective(promotionCollectiveRequest);
            if (discountType != null) {
                addPromotionRequest.setDynamicDiscountType(discountType);
            }
            if (discountValue != null) {
                addPromotionRequest.setDynamicDiscountValue(discountValue);
            }
        } else {
            addPromotionRequest.setItemIds(itemIds);
        }
        httpClient.buildRequest(HttpMethod.POST, "/orders/promotions")
                .body(new ClientRequestBody(addPromotionRequest))
                .headers(headers)
                .execute();
    }

    public void addCollective(String channelOauthToken, String cartToken, Long collectiveId, String code, String pin) {
        RequestHeaders headers = new RequestHeaders.Builder()
                .addHeader("Authorization", "Bearer " + channelOauthToken)
                .addHeader("ob-order-id", cartToken)
                .build();
        AddCollectiveRequest addCollectiveRequest = new AddCollectiveRequest();
        addCollectiveRequest.setId(collectiveId);
        addCollectiveRequest.setCode(code);
        addCollectiveRequest.setPin(pin);
        httpClient.buildRequest(HttpMethod.POST, "/orders/collectives")
                .body(new ClientRequestBody(addCollectiveRequest))
                .headers(headers)
                .execute();
    }

    private RequestHeaders.Builder prepareHeaders(String token, String orderId, String language, String previewToken) {
        RequestHeaders.Builder builder = new RequestHeaders.Builder();
        builder.addHeader(AUTHORIZATION_HEADER, "Bearer " + token);
        builder.addHeader(CONTENT_TYPE_HEADER, "application/json");
        if (orderId != null) {
            builder.addHeader(ORDER_ID_HEADER_NAME, orderId);
        }
        if (language != null) {
            builder.addHeader(ACCEPT_LANGUAGE_HEADER, language);
        }
        if (language != null) {
            builder.addHeader(OB_LANGUAGE, language);
        }
        if (previewToken != null) {
            builder.addHeader(SESSION_PREVIEW_TOKEN_HEADER, previewToken);
        }
        return builder;
    }

    public OrderResponse createOrder(String token, String language) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, null, language, null);
        HttpResponse response = httpClient.buildRequest(HttpMethod.POST, "/orders")
                .headers(headersBuilder.build())
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
        return prepareOrderResponse(response);
    }

    public OrderResponse addSeats(String token, String orderId, AddSeatsDTO addSeats, String previewToken) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, previewToken);
        HttpResponse response = httpClient.buildRequest(HttpMethod.POST, "/orders/seats")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(addSeats))
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
        return prepareOrderResponse(response);
    }

    public OrderResponse releaseSeats(String token, String orderId, Set<Long> itemIds) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, null);

        QueryParameters.Builder queryParameters = itemIds != null ? new QueryParameters.Builder() : null;
        if (itemIds != null) {
            itemIds.forEach(id -> queryParameters.addQueryParameter("id", id));
        }

        HttpResponse response = httpClient.buildRequest(HttpMethod.DELETE, "/orders/seats")
                .headers(headersBuilder.build())
                .params(queryParameters != null ? queryParameters.build() : null)
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
        return prepareOrderResponse(response);
    }

    public OrderResponse addSeatsAuto(String token, String orderId, SeatsAutoRequest seatsAutoDTO, String previewToken) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, previewToken);
        HttpResponse response = httpClient.buildRequest(HttpMethod.POST, "/orders/seats-auto")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(seatsAutoDTO))
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
        return prepareOrderResponse(response);
    }

    public OrderResponse setDeliveryMethods(String token, String orderId, DeliveryMethodsRequestDTO deliveryMethod) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, null);
        HttpResponse response = httpClient.buildRequest(HttpMethod.POST, "/orders/delivery-methods")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(deliveryMethod))
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
        return prepareOrderResponse(response);
    }

    public OrderResponse addBuyerData(String token, String orderId, Map<String, Object> buyerData) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, null);
        HttpResponse response = httpClient.buildRequest(HttpMethod.POST, "/orders/buyer-data")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(buyerData))
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
        return prepareOrderResponse(response);
    }

    public OrderResponse preConfirm(String token, String orderId, PreConfirmRequestDTO deliveryMethod) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, null);
        HttpResponse response = httpClient.buildRequest(HttpMethod.POST, "/orders/pre-confirm")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(deliveryMethod))
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
        return prepareOrderResponse(response);
    }

    public OrderResponse confirm(String token, String orderId, ConfirmRequest confirmRequest) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, null);
        HttpResponse response = httpClient.buildRequest(HttpMethod.POST, "/orders/confirm")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(confirmRequest))
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
        return prepareOrderResponse(response);
    }

    public OrderResponse addItemAttendees(String token, String orderId, ItemAttendees body) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, null);
        HttpResponse response = httpClient.buildRequest(HttpMethod.POST, "/orders/item-attendees")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(body))
                .executeWithHttpResponse(HttpResponseBodyType.STRING);
        return prepareOrderResponse(response);
    }

    public OrderResponse addRenewalSeats(String token, String orderId, RenewalSeats renewalSeats) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, null);
        return httpClient.buildRequest(HttpMethod.POST, "/orders/seats-renewal")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(renewalSeats))
                .execute(OrderResponse.class);
    }

    public OrderResponse validatePresales(String token, PresalesRequest presale, String orderId) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, null);

        if (orderId != null) {
            headersBuilder.addHeader(ORDER_ID_HEADER_NAME, orderId);
        }
        return httpClient.buildRequest(HttpMethod.POST, "/orders/presales")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(presale))
                .execute(OrderResponse.class);
    }

    public OrderResponse addInvitations(String token, String orderId, InvitationRequest invitationRequest) {
        RequestHeaders.Builder headersBuilder = prepareHeaders(token, orderId, null, null);
        return httpClient.buildRequest(HttpMethod.PUT, "/orders/invitations")
                .headers(headersBuilder.build())
                .body(new ClientRequestBody(invitationRequest))
                .execute(OrderResponse.class);
    }

    private String getResponseHeader(HttpResponse httpResponse) {
        return httpResponse.getHeaders().get("ob-audit-trace-id").toString();
    }

    private OrderResponse prepareOrderResponse(HttpResponse response) {
        String traceId = getResponseHeader(response);
        OrderResponse orderResponse = null;
        try {
            orderResponse = jacksonMapper.readValue(response.getBodyAsString(), OrderResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        orderResponse.setTraceId(traceId);
        return orderResponse;
    }

}
