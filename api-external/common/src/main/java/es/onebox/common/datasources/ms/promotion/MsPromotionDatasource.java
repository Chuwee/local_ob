package es.onebox.common.datasources.ms.promotion;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.promotion.dto.CommunicationElementDTO;
import es.onebox.common.datasources.ms.promotion.dto.CommunicationElementsListDTO;
import es.onebox.common.datasources.ms.promotion.dto.EventPromotionPriceTypesDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionChannelsDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionEventSessionsDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionDetailDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionRatesDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MsPromotionDatasource {

    private static final String API_VERSION = "v1";
    private static final String BASE_PATH = "/ms-promotion-api/" + API_VERSION;
    private static final String EVENT_PROMOTION_ELE_URL = "/events/{eventId}/promotions/{promotionId}";
    private static final String EVENT_PROMOTION_CHANNELS = "/channels";
    private static final String EVENT_PROMOTION_SESSIONS = "/sessions";
    private static final String EVENT_PROMOTION_PRICE_TYPES = "/price-types";
    private static final String EVENT_PROMOTION_RATES = "/rates";
    private static final String EVENT_PROMOTION_COMMUNICATION_ELE_URL = "/events/{eventId}/promotions/{promotionId}/communication-elements";

    private final HttpClient httpClient;

    @Autowired
    public MsPromotionDatasource(@Value("${clients.services.ms-promotion}") String baseUrl,
                                 ObjectMapper jacksonMapper) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .build();
    }

    public PromotionDetailDTO getEventPromotion(final Long eventId, final Long promotionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_ELE_URL)
                .pathParams(eventId, promotionId)
                .execute(PromotionDetailDTO.class);
    }

    public PromotionChannelsDTO getEventPromotionChannels(final Long eventId, final Long promotionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_ELE_URL + EVENT_PROMOTION_CHANNELS)
            .pathParams(eventId, promotionId)
            .execute(PromotionChannelsDTO.class);
    }

    public PromotionEventSessionsDTO getEventPromotionSessions(final Long eventId, final Long promotionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_ELE_URL + EVENT_PROMOTION_SESSIONS)
            .pathParams(eventId, promotionId)
            .execute(PromotionEventSessionsDTO.class);
    }

    public EventPromotionPriceTypesDTO getEventPromotionPriceTypes(final Long eventId, final Long promotionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_ELE_URL + EVENT_PROMOTION_PRICE_TYPES)
            .pathParams(eventId, promotionId)
            .execute(EventPromotionPriceTypesDTO.class);
    }

    public PromotionRatesDTO getEventPromotionRates(final Long eventId, final Long promotionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_ELE_URL + EVENT_PROMOTION_RATES)
            .pathParams(eventId, promotionId)
            .execute(PromotionRatesDTO.class);
    }

    public void putEventPromotion(final Long eventId, final Long promotionId, final PromotionDetailDTO promotion) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_PROMOTION_ELE_URL)
                .pathParams(eventId, promotionId)
                .body(new ClientRequestBody(promotion))
                .execute();
    }

    public List<CommunicationElementDTO> findCommunicationElements(final Long eventId, final Long promotionId) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_COMMUNICATION_ELE_URL)
                .pathParams(eventId, promotionId)
                .execute(CommunicationElementsListDTO.class);
    }

    public void updateCommunicationElements(final Long eventId, final Long promotionId, List<CommunicationElementDTO> elements) {
        httpClient.buildRequest(HttpMethod.POST, EVENT_PROMOTION_COMMUNICATION_ELE_URL)
                .pathParams(eventId, promotionId)
                .body(new ClientRequestBody(elements))
                .execute();
    }
}
