package es.onebox.mgmt.datasources.ms.promotion;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.datasource.http.ClientRequestBody;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.datasource.http.response.ListType;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsFilter;
import es.onebox.mgmt.common.promotions.dto.PromotionsFilter;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.promotion.dto.ClonePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.CreatePromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.EventPromotionTemplates;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplateDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplateFilter;
import es.onebox.mgmt.datasources.ms.promotion.dto.PromotionTemplates;
import es.onebox.mgmt.datasources.ms.promotion.dto.ResetDiscountEventPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionChannels;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionRates;
import es.onebox.mgmt.datasources.ms.promotion.dto.UpdateEventPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionDetail;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.ChannelPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.CreateChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionEvents;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionPriceTypes;
import es.onebox.mgmt.datasources.ms.promotion.dto.channel.UpdateChannelPromotionSessions;
import es.onebox.mgmt.datasources.ms.promotion.dto.packs.EventPromotionPacks;
import es.onebox.mgmt.datasources.ms.promotion.dto.packs.UpdateEventPromotionPacks;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.CreateProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.ProductPromotions;
import es.onebox.mgmt.datasources.ms.promotion.dto.product.UpdateProductPromotion;
import es.onebox.mgmt.datasources.ms.promotion.enums.PromotionTagType;
import es.onebox.mgmt.exception.ClientHttpExceptionBuilder;
import es.onebox.mgmt.products.promotions.dto.ProductPromotionsFilter;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MsPromotionDatasource extends MsPromotionMapping {

    private final HttpClient httpClient;

    @Autowired
    public MsPromotionDatasource(@Value("${clients.services.ms-promotion}") String baseUrl,
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

    public PromotionTemplates getEventPromotions(Long eventId, PromotionsFilter filter) {
        return this.httpClient
                .buildRequest(HttpMethod.GET, EVENT_PROMOTIONS)
                .pathParams(eventId)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("type", filter.getType())
                        .addQueryParameter("status", filter.getStatus())
                        .addQueryParameter("limit", filter.getLimit())
                        .addQueryParameter("offset", filter.getOffset())
                        .addQueryParameter("sort", filter.getSort())
                        .build())
                .execute(PromotionTemplates.class);
    }

    public EventPromotionTemplates getEventPromotionTemplates(PromotionTemplateFilter filter) {
        QueryParameters params = new QueryParameters.Builder().
                addQueryParameters(filter).
                build();

        return this.httpClient.buildRequest(HttpMethod.GET, "/events/promotionTemplates")
                .params(params)
                .execute(EventPromotionTemplates.class);
    }

    public PromotionTemplateDetail getEventPromotionTemplate(Long promotionTemplateId) {
        return this.httpClient.buildRequest(HttpMethod.GET, "/events/promotionTemplates/{promotionTemplateId}")
                .pathParams(promotionTemplateId)
                .execute(PromotionTemplateDetail.class);
    }

    public IdDTO createEventPromotionTemplate(CreatePromotion createPromotion) {
        return this.httpClient.buildRequest(HttpMethod.POST, "/events/promotionTemplates")
                .body(new ClientRequestBody(createPromotion))
                .execute(IdDTO.class);
    }

    public void deleteEventPromotionTemplate(Long promotionTemplateId) {
        httpClient.buildRequest(HttpMethod.DELETE, "/events/promotionTemplates/{promotionTemplateId}")
                .pathParams(promotionTemplateId)
                .execute();
    }

    public void updateEventPromotionTemplate(Long promotionTemplateId, UpdateEventPromotionDetail body) {
        this.httpClient.buildRequest(HttpMethod.PUT, "/events/promotionTemplates/{promotionTemplateId}")
                .pathParams(promotionTemplateId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public EventPromotionChannels getEventPromotionTemplateChannels(final Long promotionTemplateId) {
        return this.httpClient.buildRequest(HttpMethod.GET, "/events/promotionTemplates/{promotionTemplateId}/channels")
                .pathParams(promotionTemplateId)
                .execute(EventPromotionChannels.class);
    }

    public void updateEventPromotionTemplateChannels(final Long promotionTemplateId,
                                                     final UpdateEventPromotionChannels scopes) {
        httpClient.buildRequest(HttpMethod.PUT, "/events/promotionTemplates/{promotionTemplateId}/channels")
                .pathParams(promotionTemplateId)
                .body(new ClientRequestBody(scopes))
                .execute();
    }

    public List<BaseCommunicationElement> getEventPromotionTemplateChannelContentTexts(Long promotionTemplateId, CommunicationElementFilter<PromotionTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, "/events/promotionTemplates/{promotionTemplateId}/communication-elements")
                .pathParams(promotionTemplateId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(BaseCommunicationElement.class));
    }

    public void updateEventPromotionTemplateCommunicationElements(Long promotionTemplateId, List<BaseCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, "/events/promotionTemplates/{promotionTemplateId}/communication-elements")
                .pathParams(promotionTemplateId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public PromotionDetail getEventPromotion(Long eventId, Long eventPromotionId) {
        return this.httpClient
                .buildRequest(HttpMethod.GET, EVENT_PROMOTION)
                .pathParams(eventId, eventPromotionId)
                .execute(PromotionDetail.class);
    }

    public void updateEventPromotion(Long eventId, Long eventPromotionId, UpdateEventPromotionDetail body) {
        this.httpClient.buildRequest(HttpMethod.PUT, EVENT_PROMOTION).body(new ClientRequestBody(body))
                .pathParams(eventId, eventPromotionId)
                .execute();
    }

    public EventPromotionChannels getEventPromotionChannels(final Long eventId, final Long eventPromotionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_CHANNELS)
                .pathParams(eventId, eventPromotionId).execute(EventPromotionChannels.class);
    }

    public void updateEventPromotionChannels(final Long eventId, final Long eventPromotionId,
                                             final UpdateEventPromotionChannels scopes) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_PROMOTION_CHANNELS).pathParams(eventId, eventPromotionId)
                .body(new ClientRequestBody(scopes)).execute();
    }

    public EventPromotionSessions getEventPromotionSessions(final Long eventId, final Long eventPromotionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_SESSIONS)
                .pathParams(eventId, eventPromotionId).execute(EventPromotionSessions.class);
    }

    public void updateEventPromotionSessions(final Long eventId, final Long eventPromotionId,
                                             final UpdateEventPromotionSessions scopes) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_PROMOTION_SESSIONS).pathParams(eventId, eventPromotionId)
                .body(new ClientRequestBody(scopes)).execute();
    }

    public EventPromotionPriceTypes getEventPromotionPriceTypes(final Long eventId, final Long eventPromotionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_PRICE_TYPES)
                .pathParams(eventId, eventPromotionId).execute(EventPromotionPriceTypes.class);
    }

    public void updateEventPromotionPriceTypes(final Long eventId, final Long eventPromotionId,
                                               final UpdateEventPromotionPriceTypes scopes) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_PROMOTION_PRICE_TYPES).pathParams(eventId, eventPromotionId)
                .body(new ClientRequestBody(scopes)).execute();
    }

    public EventPromotionRates getEventPromotionRates(final Long eventId, final Long eventPromotionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_RATES).pathParams(eventId, eventPromotionId)
                .execute(EventPromotionRates.class);
    }

    public void updateEventPromotionRates(final Long eventId, final Long eventPromotionId,
                                          final UpdateEventPromotionRates scopes) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_PROMOTION_RATES).pathParams(eventId, eventPromotionId)
                .body(new ClientRequestBody(scopes)).execute();
    }

    public EventPromotionPacks getEventPromotionPacks(final Long eventId, final Long eventPromotionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, EVENT_PROMOTION_PACKS).pathParams(eventId, eventPromotionId)
                .execute(EventPromotionPacks.class);
    }

    public void updateEventPromotionPacks(final Long eventId, final Long eventPromotionId,
                                          final UpdateEventPromotionPacks request) {
        httpClient.buildRequest(HttpMethod.PUT, EVENT_PROMOTION_PACKS).pathParams(eventId, eventPromotionId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public IdDTO createEventPromotion(Long eventId, CreatePromotion createPromotion) {
        return this.httpClient.buildRequest(HttpMethod.POST, EVENT_PROMOTIONS)
                .pathParams(eventId)
                .body(new ClientRequestBody(createPromotion))
                .execute(IdDTO.class);
    }

    public List<IdDTO> cloneEventPromotion(Long eventId, List<ClonePromotion> clonePromotion) {
        return this.httpClient.buildRequest(HttpMethod.POST, EVENT_PROMOTIONS_CLONE).pathParams(eventId)
                .body(new ClientRequestBody(clonePromotion))
                .execute(ListType.of(IdDTO.class));
    }

    public List<IdDTO> cloneEntityPromotion(Long eventId, List<ClonePromotion> clonePromotion) {
        return this.httpClient.buildRequest(HttpMethod.POST, EVENT_PROMOTIONS_CLONE).pathParams(eventId)
                .body(new ClientRequestBody(clonePromotion))
                .execute(ListType.of(IdDTO.class));
    }

    public void cloneEntityPromotions(Long eventId, List<ClonePromotion> clonePromotion) {
        this.httpClient.buildRequest(HttpMethod.POST, EVENT_PROMOTIONS_CLONE).pathParams(eventId)
                .body(new ClientRequestBody(clonePromotion))
                .execute();
    }

    public List<BaseCommunicationElement> getEventCommunicationElements(Long eventId,
                                                                        Long promotionId, CommunicationElementFilter<PromotionTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, EVENT_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, promotionId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(BaseCommunicationElement.class));
    }

    public void updateEventCommunicationElements(Long eventId, Long promotionId, List<BaseCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, EVENT_COMMUNICATION_ELEMENTS)
                .pathParams(eventId, promotionId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public void resetDiscountEventPromotions(Long eventId, ResetDiscountEventPromotions resetDiscountEventPromotions) {
        httpClient.buildRequest(HttpMethod.PATCH, EVENT_PROMOTIONS_RESET_PROMOTION_DISCOUNT_CONFIG)
                .pathParams(eventId)
                .body(new ClientRequestBody(resetDiscountEventPromotions))
                .execute();
    }

    public void deleteEventPromotion(Long eventId, Long eventPromotionId) {
        httpClient.buildRequest(HttpMethod.DELETE, EVENT_PROMOTION)
                .pathParams(eventId, eventPromotionId)
                .execute();
    }

    public ChannelPromotions getChannelPromotions(Long channelId, ChannelPromotionsFilter filter) {
        return this.httpClient
                .buildRequest(HttpMethod.GET, CHANNEL_PROMOTIONS)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("type", filter.getType())
                        .addQueryParameter("status", filter.getStatus())
                        .addQueryParameter("limit", filter.getLimit())
                        .addQueryParameter("offset", filter.getOffset())
                        .addQueryParameter("sort", filter.getSort())
                        .build())
                .pathParams(channelId)
                .execute(ChannelPromotions.class);
    }

    public ChannelPromotionDetail getChannelPromotion(Long channelId, Long promotionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, CHANNEL_PROMOTION)
                .pathParams(channelId, promotionId)
                .execute(ChannelPromotionDetail.class);
    }

    public IdDTO createChannelPromotion(Long channelId, CreateChannelPromotion request) {
        return this.httpClient.buildRequest(HttpMethod.POST, CHANNEL_PROMOTIONS)
                .pathParams(channelId)
                .body(new ClientRequestBody(request))
                .execute(IdDTO.class);
    }

    public void updateChannelPromotion(Long channelId, Long promotionId, UpdateChannelPromotion request) {
        this.httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PROMOTION)
                .pathParams(channelId, promotionId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteChannelPromotion(Long channelId, Long promotionId) {
        httpClient.buildRequest(HttpMethod.DELETE, CHANNEL_PROMOTION)
                .pathParams(channelId, promotionId)
                .execute();
    }

    public ChannelPromotionEvents getChannelPromotionEvents(Long channelId, Long promotionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, CHANNEL_PROMOTION_EVENTS)
                .pathParams(channelId, promotionId)
                .execute(ChannelPromotionEvents.class);
    }

    public void updateChannelPromotionEvents(Long channelId, Long promotionId, UpdateChannelPromotionEvents scopes) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PROMOTION_EVENTS)
                .pathParams(channelId, promotionId)
                .body(new ClientRequestBody(scopes)).execute();
    }

    public ChannelPromotionSessions getChannelPromotionSessions(Long channelId, Long promotionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, CHANNEL_PROMOTION_SESSIONS)
                .pathParams(channelId, promotionId)
                .execute(ChannelPromotionSessions.class);
    }

    public void updateChannelPromotionSessions(Long channelId, Long promotionId, UpdateChannelPromotionSessions scopes) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PROMOTION_SESSIONS)
                .pathParams(channelId, promotionId)
                .body(new ClientRequestBody(scopes)).execute();
    }

    public List<BaseCommunicationElement> getChannelCommunicationElements(Long channelId, Long promotionId,
                                                                          CommunicationElementFilter<PromotionTagType> filter) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_PROMOTION_COMMUNICATION_ELEMENTS)
                .pathParams(channelId, promotionId)
                .params(new QueryParameters.Builder().addQueryParameters(filter).build())
                .execute(ListType.of(BaseCommunicationElement.class));
    }

    public void updateChannelCommunicationElements(Long channelId, Long promotionId, List<BaseCommunicationElement> elements) {
        httpClient.buildRequest(HttpMethod.POST, CHANNEL_PROMOTION_COMMUNICATION_ELEMENTS)
                .pathParams(channelId, promotionId)
                .body(new ClientRequestBody(elements))
                .execute();
    }

    public IdDTO cloneChannelPromotion(Long channelId, CreateChannelPromotion request) {
        return httpClient.buildRequest(HttpMethod.POST, CHANNEL_PROMOTIONS)
                .pathParams(channelId)
                .body(new ClientRequestBody(request))
                .execute(IdDTO.class);
    }

    public ChannelPromotionPriceTypes getChannelPromotionPriceTypes(Long channelId, Long promotionId) {
        return httpClient.buildRequest(HttpMethod.GET, CHANNEL_PROMOTION_PRICE_TYPES)
                .pathParams(channelId, promotionId)
                .execute(ChannelPromotionPriceTypes.class);
    }

    public void updateChannelPromotionPriceTypes(Long channelId, Long promotionId, UpdateChannelPromotionPriceTypes body) {
        httpClient.buildRequest(HttpMethod.PUT, CHANNEL_PROMOTION_PRICE_TYPES)
                .pathParams(channelId, promotionId)
                .body(new ClientRequestBody(body))
                .execute();
    }

    public ProductPromotions getProductPromotions(Long productId, ProductPromotionsFilter filter) {
        return this.httpClient
                .buildRequest(HttpMethod.GET, PRODUCT_PROMOTIONS)
                .params(new QueryParameters.Builder()
                        .addQueryParameter("type", filter.getType())
                        .addQueryParameter("status", filter.getStatus())
                        .addQueryParameter("limit", filter.getLimit())
                        .addQueryParameter("offset", filter.getOffset())
                        .addQueryParameter("sort", filter.getSort())
                        .build())
                .pathParams(productId)
                .execute(ProductPromotions.class);
    }

    public ProductPromotion getProductPromotion(Long productId, Long promotionId) {
        return this.httpClient.buildRequest(HttpMethod.GET, PRODUCT_PROMOTION)
                .pathParams(productId, promotionId)
                .execute(ProductPromotion.class);
    }

    public IdDTO createProductPromotion(Long productId, CreateProductPromotion request) {
        return this.httpClient.buildRequest(HttpMethod.POST, PRODUCT_PROMOTIONS)
                .pathParams(productId)
                .body(new ClientRequestBody(request))
                .execute(IdDTO.class);
    }

    public void updateProductPromotion(Long productId, Long promotionId, UpdateProductPromotion request) {
        this.httpClient.buildRequest(HttpMethod.PUT, PRODUCT_PROMOTION)
                .pathParams(productId, promotionId)
                .body(new ClientRequestBody(request))
                .execute();
    }

    public void deleteProductPromotion(Long productId, Long promotionId) {
        httpClient.buildRequest(HttpMethod.DELETE, PRODUCT_PROMOTION)
                .pathParams(productId, promotionId)
                .execute();
    }
}

