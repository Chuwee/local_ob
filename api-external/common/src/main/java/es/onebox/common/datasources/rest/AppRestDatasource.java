package es.onebox.common.datasources.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import es.onebox.common.datasources.rest.dto.ns.data_query.session.SessionInfo;
import es.onebox.common.datasources.rest.dto.ns.shopping.cart.PromotionalCodeApplicableGroup;
import es.onebox.common.datasources.rest.dto.ns.shopping.cart.ShoppingCart;
import es.onebox.common.datasources.rest.dto.ns.shopping.cart.UserApplicableGroup;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppRestDatasource {

    private static final String BASE_PATH = "/onebox-rest2/rest";
    private static final int TIMEOUT = 60000;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final String CART_PATH = "/shoppingCart" ;

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final XmlMapper xmlMapper;
    
    protected AppRestDatasource(@Value("${clients.services.app-rest}") String appRestUrl,
                                TracingInterceptor tracingInterceptor,
                                ObjectMapper jacksonMapper) {
        this.objectMapper = jacksonMapper;
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(appRestUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor, new AppRestRequestInterceptor(), new AppRestErrorInterceptor())
                .connectTimeout(CONNECT_TIMEOUT)
                .readTimeout(TIMEOUT)
                .build();
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public SessionInfo getSessionInfo(Long sessionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, "/session/{sessionId}/info?showNotPublishedInfo=false")
                .pathParams(sessionId)
                .execute(SessionInfo.class);
    }

    public void validateCart(String cartId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("token", cartId)
                .build();
        this.httpClient.buildRequest(HttpMethod.POST, CART_PATH + "/validate")
                .params(params)
                .execute();
    }

    public UserApplicableGroup usernameGroupValidation(String cartId, Long collectiveId, String username, String password, Long sessionId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("token", cartId)
                .addQueryParameter("idGroup", collectiveId)
                .addQueryParameter("user", username)
                .addQueryParameter("password", password)
                .addQueryParameter("sessionId", sessionId)
                .build();
        return this.httpClient.buildRequest(HttpMethod.POST, CART_PATH + "/usernameGroupValidation")
                .params(params)
                .execute(UserApplicableGroup.class);
    }

    public ShoppingCart releaseAllSeats(String cartId) {
        Map<String, Object> params = new HashMap<>();
        params.put("token", cartId);
        return this.httpClient.buildRequest(HttpMethod.POST, CART_PATH + "/releaseAllItems")
                .headers(new RequestHeaders.Builder()
                        .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .build())
                .body(new ClientRequestBody(params, ClientRequestBody.Type.FORM))
                .execute(ShoppingCart.class);
    }

    public ShoppingCart addActivitySeats(String cartId, Long sessionId, Integer numSeats, Long idActivity) {
        Map<String, Object> params = new HashMap<>();
        params.put("token", cartId);
        params.put("idSession", sessionId);
        params.put("numSeats", numSeats);
        params.put("idActivityTicketType", idActivity);
        return this.httpClient.buildRequest(HttpMethod.POST, CART_PATH + "/addIndividualActivitySeats")
                .body(new ClientRequestBody(params, ClientRequestBody.Type.FORM))
                .execute(ShoppingCart.class);
    }

    public ShoppingCart releaseSeats(String cartId, List<Long> itemIds) {
        QueryParameters.Builder paramsBuilder = new QueryParameters.Builder()
                .addQueryParameter("token", cartId);
        for (Long itemId : itemIds) {
            paramsBuilder.addQueryParameter("items", itemId);
        }
        return this.httpClient.buildRequest(HttpMethod.POST, CART_PATH + "/releaseItems")
                .params(paramsBuilder.build())
                .execute(ShoppingCart.class);
    }


    public ShoppingCart availablePromotionsAndDiscounts(String cartId) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("token", cartId)
                .build();
        return this.httpClient.buildRequest(HttpMethod.GET, CART_PATH + "/availablePromotionsAndDiscounts")
                .params(params)
                .execute(ShoppingCart.class);
    }

    public PromotionalCodeApplicableGroup promotionalCodeGroup(String cartId, Long collectiveId, String code) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("token", cartId)
                .addQueryParameter("idGroup", collectiveId)
                .addQueryParameter("promotionalCode", code)
                .build();
        return this.httpClient.buildRequest(HttpMethod.POST, CART_PATH + "/promotionalCodeGroup")
                .params(params)
                .execute(PromotionalCodeApplicableGroup.class);
    }

}
