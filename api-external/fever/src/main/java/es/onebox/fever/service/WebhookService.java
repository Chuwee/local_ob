package es.onebox.fever.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.datasources.ms.channel.ChannelConfigResponse;
import es.onebox.common.datasources.ms.channel.MsChannelDatasource;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.common.datasources.ms.channel.filter.ChannelConfigsFilter;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.dto.EventChannelsDTO;
import es.onebox.common.datasources.ms.event.dto.ProductChannelsDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.notification.repository.MsNotificationRepository;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.webhook.WebhookDatasource;
import es.onebox.common.datasources.webhook.dto.fever.FeverMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.NotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.common.datasources.webhook.dto.fever.webhook.FeverKafkaMessage;
import es.onebox.common.datasources.webhook.dto.fever.webhook.FeverKafkaPayload;
import es.onebox.common.datasources.webhook.dto.fever.webhook.FeverKafkaSchema;
import es.onebox.common.datasources.webhook.dto.fever.webhook.FeverKafkaSchemaField;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.GeneratorUtils;
import es.onebox.common.utils.WebhookUtils;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.mapper.JsonMapper;
import es.onebox.datasource.http.RequestHeaders.Builder;
import es.onebox.fever.config.FeverConfig;
import es.onebox.fever.config.FeverWebhookConfiguration;
import es.onebox.fever.converter.CommonConverter;
import es.onebox.fever.dto.enums.NotificationSubtype;
import es.onebox.fever.repository.AllowedEntitiesRepository;
import es.onebox.message.broker.kafka.DefaultKafkaProducer;
import es.onebox.message.broker.kafka.exception.KafkaClientException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class WebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookService.class);

    private static final FeverKafkaSchema KAFKA_SCHEMA = buildKafkaSchema();

    private final WebhookDatasource webhookDatasource;
    private final MsNotificationRepository msNotificationRepository;
    private final PromotionWebhookService promotionWebhookService;
    private final EventWebhookService eventWebhookService;
    private final SessionWebhookService sessionWebhookService;
    private final OrderWebhookService orderWebhookService;
    private final ProductWebhookService productWebhookService;
    private final MsEventRepository msEventRepository;
    private final MsOrderRepository msOrderRepository;
    private final MsChannelDatasource msChannelDatasource;
    private final ChannelWebhookService channelWebhookService;
    private final FeverWebhookConfiguration feverWebhookConfiguration;
    private final AllowedEntitiesRepository allowedEntitiesRepository;
    private final ChannelRepository channelRepository;
    private final EntityWebhookService entityWebhookService;
    private final UserWebhookService userWebhookService;
    private final DefaultKafkaProducer feverWebhookProducer;
    private final ObjectMapper jacksonMapper;

    @Value("${fever.entity.entityId}")
    private String feverEntityId;

    @Autowired
    public WebhookService(WebhookDatasource webhookDatasource,
                          MsNotificationRepository msNotificationRepository,
                          PromotionWebhookService promotionWebhookService,
                          EventWebhookService eventWebhookService,
                          ProductWebhookService productWebhookService,
                          MsEventRepository msEventRepository,
                          MsOrderRepository msOrderRepository,
                          MsChannelDatasource msChannelDatasource,
                          SessionWebhookService sessionWebhookService,
                          OrderWebhookService orderWebhookService,
                          ChannelWebhookService channelWebhookService,
                          FeverWebhookConfiguration feverWebhookConfiguration,
                          UserWebhookService userWebhookService,
                          AllowedEntitiesRepository allowedEntitiesRepository,
                          ChannelRepository channelRepository,
                          EntityWebhookService entityWebhookService,
                          DefaultKafkaProducer feverWebhookProducer) {
        this.webhookDatasource = webhookDatasource;
        this.msNotificationRepository = msNotificationRepository;
        this.promotionWebhookService = promotionWebhookService;
        this.eventWebhookService = eventWebhookService;
        this.productWebhookService = productWebhookService;
        this.sessionWebhookService = sessionWebhookService;
        this.orderWebhookService = orderWebhookService;
        this.channelWebhookService = channelWebhookService;
        this.msEventRepository = msEventRepository;
        this.msOrderRepository = msOrderRepository;
        this.msChannelDatasource = msChannelDatasource;
        this.feverWebhookConfiguration = feverWebhookConfiguration;
        this.allowedEntitiesRepository = allowedEntitiesRepository;
        this.channelRepository = channelRepository;
        this.entityWebhookService = entityWebhookService;
        this.userWebhookService = userWebhookService;
        this.feverWebhookProducer = feverWebhookProducer;
        this.jacksonMapper = JsonMapper.jacksonMapper();
    }


    public void sendWebhookToFever(WebhookFeverDTO webhookFever) throws JsonProcessingException {

        String obEvent = getEventAndValidateMessage(webhookFever);
        LOGGER.info(WebhookUtils.buildProcessingMessage(webhookFever));

        webhookFever.setAllowedEntitiesFileData(allowedEntitiesRepository.getAllowedEntitiesFileData(feverEntityId));

        switch (obEvent) {
            case "EVENT_UPDATE":
                handleEventWebhook(webhookFever);
                break;
            case "SESSION_UPDATE":
                handleSessionWebhook(webhookFever);
                break;
            case "ORDER":
                handleOrderWebhook(webhookFever);
                break;
            case "CHANNEL":
                handleChannelWebhook(webhookFever);
                break;
            case "PROMOTION":
                handlePromotionWebhook(webhookFever);
                break;
            case "PRODUCT":
                handleProductWebhook(webhookFever);
                break;
            case "ENTITY_FVZONE":
                handleEntityFvZoneWebhook(webhookFever);
                break;
            case "USER_FVZONE":
                handleUserFvZoneWebhook(webhookFever);
                break;
            default:
                sendDefaultFeverWebhook(webhookFever);
        }
    }

    private void handleEventWebhook(WebhookFeverDTO webhookFever) throws JsonProcessingException {

        String obSubtype = webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_SUBTYPE);

        if (Objects.isNull(obSubtype)) {
            sendDefaultFeverWebhook(webhookFever);

      /* TODO Implement when all event_updates are informed with subtype
      throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
      */
            return;
        }

        validateIfNeedsNotification(Long.valueOf(webhookFever.getNotificationMessage().getId()), webhookFever);

        if (!webhookFever.getAllowSend()) {
            LOGGER.info(WebhookUtils.buildDiscardingMessage(webhookFever));
            return;
        }

        switch (obSubtype) {
            case "EVENT_GENERAL_DATA" -> sendFeverWebhook(eventWebhookService.sendEventGeneralData(webhookFever));
            case "EVENT_COMMUNICATION_IMAGES", "EVENT_COMMUNICATION_TEXTS" ->
                    sendFeverWebhook(eventWebhookService.sendEventCommunication(webhookFever, obSubtype));
            case "EVENT_CREATED", "PROMOTION_DELETED", "EVENT_RATE_DELETED" -> sendDefaultFeverWebhook(webhookFever);
            case "EVENT_SURCHARGES" -> sendFeverWebhook(eventWebhookService.sendEventSurcharges(webhookFever));
            case "EVENT_CHANNEL_SURCHARGES" ->
                    sendFeverWebhook(eventWebhookService.sendEventChannelSurcharges(webhookFever));
            case "EVENT_RATE_DETAIL" -> sendFeverWebhook(eventWebhookService.sendEventRateDetail(webhookFever));
            case "EVENT_PRICE_TYPE" -> sendFeverWebhook(eventWebhookService.sendEventVenueTemplatePrices(webhookFever));
            case "EVENT_PRICE_TYPE_DETAIL", "EVENT_PRICE_TYPE_CREATED" ->
                    sendFeverWebhook(eventWebhookService.sendEventPriceTypeDetail(webhookFever));
            case "EVENT_PRICE_TYPE_DELETED" ->
                    sendFeverWebhook(eventWebhookService.sendEventPriceTypeDeleted(webhookFever));
            case "EVENT_PRICE_TYPE_COMMUNICATION" ->
                    sendFeverWebhook(eventWebhookService.sendEventPriceTypeCommunication(webhookFever));
            case "PROMOTION_DETAIL", "PROMOTION_INACTIVE" ->
                    sendFeverWebhook(promotionWebhookService.sendPromotionDetail(webhookFever));
            case "PROMOTION_CHANNEL" -> sendFeverWebhook(promotionWebhookService.sendPromotionChannel(webhookFever));
            case "PROMOTION_SESSION" -> sendFeverWebhook(promotionWebhookService.sendPromotionSession(webhookFever));
            case "PROMOTION_PRICE_TYPE" ->
                    sendFeverWebhook(promotionWebhookService.sendPromotionPriceTypes(webhookFever));
            case "PROMOTION_RATE" -> sendFeverWebhook(promotionWebhookService.sendPromotionRates(webhookFever));
            case "PROMOTION_CHANNEL_DETAIL" ->
                    sendFeverWebhook(promotionWebhookService.sendPromotionChannelDetail(webhookFever));
            default -> throw new OneboxRestException(ApiExternalErrorCode.SUBTYPE_NOT_EXIST);
        }
    }

    private void handleSessionWebhook(WebhookFeverDTO webhookFever) throws JsonProcessingException {
        String obSubtype = webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_SUBTYPE);

        if (Objects.isNull(obSubtype)) {
            sendDefaultFeverWebhook(webhookFever);

      /* TODO Implement when all session_updates are informed with subtype
      throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
      */
            return;
        }

        try {
            SessionDTO session = msEventRepository.getSessionCached(Long.valueOf(webhookFever.getNotificationMessage().getId()));

            if (session != null) {
                validateIfNeedsNotification(session.getEventId(), webhookFever);
            } else {
                webhookFever.setAllowSend(false);
            }
        } catch (Exception e) {
            if (e.getMessage().equals(ApiExternalErrorCode.NOT_FOUND.getMessage()) && NotificationSubtype.SESSION_DELETED.name().equals(obSubtype)) {
                validateIfNeedsNotification(webhookFever.getNotificationMessage().getEventId(), webhookFever);
            } else {
                throw e;
            }
        }

        if (!webhookFever.getAllowSend()) {
            LOGGER.info(WebhookUtils.buildDiscardingMessage(webhookFever));
            return;
        }

        switch (obSubtype) {
            case "SESSION_GENERAL_DATA", "SESSION_CREATED" ->
                    sendFeverWebhook(sessionWebhookService.sendSessionGeneralData(webhookFever));
            case "SESSION_DELETED" -> sendDefaultFeverWebhook(webhookFever);
            case "SESSION_COMMUNICATION_TEXTS", "SESSION_COMMUNICATION_IMAGES", "SESSION_COMMUNICATION_DELETED" ->
                    sendFeverWebhook(sessionWebhookService.sendSessionCommunicationElements(webhookFever));
            default -> throw new OneboxRestException(ApiExternalErrorCode.SUBTYPE_NOT_EXIST);
        }
    }

    private void handleOrderWebhook(WebhookFeverDTO webhookFever) throws JsonProcessingException {
        String obAction = webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_ACTION);


        if (Objects.isNull(obAction)) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        }

        validateIfOrderNeedsNotification(webhookFever);

        if (!webhookFever.getAllowSend()) {
            LOGGER.info(WebhookUtils.buildDiscardingMessage(webhookFever));
            return;
        }

        switch (obAction) {
            case "REFUND" -> sendFeverWebhook(orderWebhookService.sendOrderDetail(webhookFever));
            case "PRINT" -> sendFeverWebhook(orderWebhookService.sendPDFGenerationData(webhookFever));
            default -> sendDefaultFeverWebhook(webhookFever);
        }
    }

    private void handlePromotionWebhook(WebhookFeverDTO webhookFever) throws JsonProcessingException {
        String obAction = webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_ACTION);

        if (Objects.isNull(obAction)) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        }
        validateIfNeedsNotification(webhookFever.getNotificationMessage().getEventId(), webhookFever);

        if (!webhookFever.getAllowSend()) {
            LOGGER.info(WebhookUtils.buildDiscardingMessage(webhookFever));
            return;
        }

        if (!"UPDATE".equals(obAction)) {
            sendDefaultFeverWebhook(webhookFever);
            return;
        }
        sendFeverWebhook(promotionWebhookService.sendPromotionLimits(webhookFever));
    }

    private void handleChannelWebhook(WebhookFeverDTO webhookFever) throws JsonProcessingException {
        String obAction = webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_ACTION);
        if (Objects.isNull(obAction)) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        }

        if (!"UPDATE".equals(obAction)) {
            sendDefaultFeverWebhook(webhookFever);
            return;
        }

        String obSubtype = webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_SUBTYPE);
        if (Objects.isNull(obSubtype)) {
            sendDefaultFeverWebhook(webhookFever);
            return;
        }

        switch (obSubtype) {
            case "CHANNEL_FORM" -> sendFeverWebhook(channelWebhookService.sendChannelFormUpdateDetail(webhookFever));
            case "SALE_REQUEST_STATUS" ->
                    sendFeverWebhook(channelWebhookService.sendChannelSaleRequestUpdateDetail(webhookFever));
            case "CHANNEL_REQUIRED_EVENTS" ->
                    sendFeverWebhook(channelWebhookService.sendChannelRequiredEventsUpdateDetail(webhookFever));
            default -> throw new OneboxRestException(ApiExternalErrorCode.SUBTYPE_NOT_EXIST);
        }
    }

    private void handleEntityFvZoneWebhook(WebhookFeverDTO webhookFever) throws JsonProcessingException {
        sendFeverWebhook(entityWebhookService.sendEntityFvZoneData(webhookFever));
    }

    private void handleUserFvZoneWebhook(WebhookFeverDTO webhookFever) throws JsonProcessingException {
        sendFeverWebhook(userWebhookService.sendUserFvZoneData(webhookFever));
    }

    private void handleProductWebhook(WebhookFeverDTO webhookFever) throws JsonProcessingException {
        String obSubtype = webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_SUBTYPE);

        validateIfProductNeedsNotification(webhookFever, obSubtype);
        switch (obSubtype) {
            case "PRODUCT_GENERAL_DATA" -> sendFeverWebhook(productWebhookService.sendProductGeneralData(webhookFever));
            case "PRODUCT_SURCHARGES" -> sendFeverWebhook(productWebhookService.sendProductSurcharges(webhookFever));
            case "PRODUCT_CONFIGURATION" ->
                    sendFeverWebhook(productWebhookService.sendProductConfiguration(webhookFever));
            case "PRODUCT_LANGUAGES" -> sendFeverWebhook(productWebhookService.sendProductLanguages(webhookFever));
            case "PRODUCT_CHANNEL_ADDED", "PRODUCT_CHANNEL_DELETED" ->
                    sendFeverWebhook(productWebhookService.sendProductChannelsUpdate(webhookFever));
            case "PRODUCT_CHANNEL_SALE_TYPE", "PRODUCT_CHANNEL_SALE_REQUEST" ->
                    sendFeverWebhook(productWebhookService.sendProductChannelUpdateSale(webhookFever));
            case "PRODUCT_EVENTS" -> sendFeverWebhook(productWebhookService.sendProductEvents(webhookFever));
            case "PRODUCT_SESSIONS" -> sendFeverWebhook(productWebhookService.sendProductSessions(webhookFever));
            case "PRODUCT_CHANNEL_LITERALS" ->
                    sendFeverWebhook(productWebhookService.sendProductChannelLiterals(webhookFever));
            case "PRODUCT_CHANNEL_IMAGES" ->
                    sendFeverWebhook(productWebhookService.sendProductChannelImages(webhookFever));
            default -> throw new OneboxRestException(ApiExternalErrorCode.SUBTYPE_NOT_EXIST);
        }

    }

    private void validateIfProductNeedsNotification(WebhookFeverDTO webhookFever, String obSubtype) {
        Long productId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        ProductChannelsDTO productChannels = msEventRepository.getProductChannels(productId);

        List<Long> channelIds = resolveChannelIds(productChannels, webhookFever, obSubtype);

        if (channelIds.isEmpty()) {
            webhookFever.setAllowSend(false);
            return;
        }

        webhookFever.setAllowSend(isAllowedToBeSent(channelIds, webhookFever));
    }

    private List<Long> resolveChannelIds(ProductChannelsDTO productChannels, WebhookFeverDTO webhookFever, String obSubtype) {
        if (CollectionUtils.isEmpty(productChannels)) {
            if ("PRODUCT_CHANNEL_DELETED".equals(obSubtype)) {
                Long deletedChannelId = webhookFever.getNotificationMessage().getChannelId();
                if (deletedChannelId != null) {
                    return List.of(deletedChannelId);
                }
            }
            return List.of();
        }

        return productChannels.stream()
                .map(productChannel -> productChannel.getChannel().getId())
                .filter(Objects::nonNull)
                .toList();
    }

    private void validateIfNeedsNotification(Long eventId, WebhookFeverDTO webhookFever) {

        EventChannelsDTO eventChannels = msEventRepository.getEventChannels(eventId);

        if (eventChannels == null) {
            webhookFever.setAllowSend(false);
            return;
        }

        List<Long> channelIds = eventChannels.getData().stream()
                .map(eventChannel -> eventChannel.getChannel().getId())
                .filter(Objects::nonNull)
                .toList();

        boolean allowSend = isAllowedToBeSent(channelIds, webhookFever);
        webhookFever.setAllowSend(allowSend);
    }

    private boolean isAllowedToBeSent(List<Long> channelIds, WebhookFeverDTO webhookFever) {
        ChannelConfigsFilter filter = new ChannelConfigsFilter();
        filter.setChannelIds(channelIds);
        ChannelConfigResponse channelConfigs = msChannelDatasource.getChannelConfigs(filter);

        return channelConfigs.getData().stream().anyMatch(channelConfig ->
                webhookFever.getAllowedEntitiesFileData().getAllowedEntities().contains(channelConfig.getEntityId())
                        && WhitelabelType.EXTERNAL.equals(channelConfig.getWhitelabelType())
                        || webhookFever.getAllowedEntitiesFileData().getEntityId().equals(channelConfig.getEntityId()));

    }

    private void validateIfOrderNeedsNotification(WebhookFeverDTO webhookFever) {

        OrderDTO order = msOrderRepository.getOrderByCodeCached(
                webhookFever.getNotificationMessage().getCode());

        if (order != null) {
            ChannelDTO channel = channelRepository.getChannel(Long.valueOf(order.getOrderData().getChannelId()));
            if ((WhitelabelType.EXTERNAL.equals(channel.getWhitelabelType())
                    && webhookFever.getAllowedEntitiesFileData().getAllowedEntities().contains(channel.getEntityId())) ||
                    webhookFever.getAllowedEntitiesFileData().getEntityId().equals(channel.getEntityId())) {
                webhookFever.setAllowSend(true);
                return;
            }
            webhookFever.setAllowSend(false);
        }
    }

    private void sendFeverWebhook(WebhookFeverDTO webhookFever)
            throws JsonProcessingException {

        if (!webhookFever.getAllowSend()) {
            LOGGER.info(WebhookUtils.buildDiscardingMessage(webhookFever));
            return;
        }

        Builder requestHeaders = new Builder();

        if (!webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_EVENT).equals("PROMOTION")) {
            webhookFever.getNotificationMessage().setPromotionActive(null);
        }

        requestHeaders.addHeader(WebhookUtils.HEADER_OB_SIGNATURE, generateSignature(feverWebhookConfiguration.getHashKey(), webhookFever.getFeverMessage()));

        Optional.ofNullable(webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_DELIVERY_ID))
                .ifPresent(header -> requestHeaders.addHeader(WebhookUtils.HEADER_OB_DELIVERY_ID, header));

        requestHeaders.addHeader(WebhookUtils.HEADER_OB_ACTION, webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_ACTION));
        Optional.ofNullable(webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_SUBTYPE))
                .ifPresent(header -> requestHeaders.addHeader(WebhookUtils.HEADER_OB_SUBTYPE, header));
        requestHeaders.addHeader(WebhookUtils.HEADER_OB_EVENT, getObEvent(webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_EVENT)));
        requestHeaders.addHeader(WebhookUtils.HEADER_OB_HOOK_ID, feverWebhookConfiguration.getHookId());

        webhookDatasource.sendFeverMessage(webhookFever, requestHeaders.build(), feverWebhookConfiguration.getUrl());

        sendFeverKafkaMessage(webhookFever);
    }


    private void sendDefaultFeverWebhook(WebhookFeverDTO webhookFever)
            throws JsonProcessingException {

        Builder requestHeaders = new Builder();

        if (!webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_EVENT).equals("PROMOTION")) {
            webhookFever.getNotificationMessage().setPromotionActive(null);
        }

        requestHeaders.addHeader(WebhookUtils.HEADER_OB_SIGNATURE, generateSignature(feverWebhookConfiguration.getHashKey(), webhookFever.getNotificationMessage()));

        Optional.ofNullable(webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_DELIVERY_ID))
                .ifPresent(header -> requestHeaders.addHeader(WebhookUtils.HEADER_OB_DELIVERY_ID, header));

        requestHeaders.addHeader(WebhookUtils.HEADER_OB_ACTION, webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_ACTION));
        requestHeaders.addHeader(WebhookUtils.HEADER_OB_EVENT, webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_EVENT));
        requestHeaders.addHeader(WebhookUtils.HEADER_OB_HOOK_ID, feverWebhookConfiguration.getHookId());

        webhookFever.setFeverMessage(CommonConverter.convert(webhookFever.getNotificationMessage()));

        webhookDatasource.sendFeverDefaultMessage(webhookFever, requestHeaders.build(), feverWebhookConfiguration.getUrl());
    }

    private void sendFeverKafkaMessage(WebhookFeverDTO message) {
        try {
            String deliveryId = message.getHeaders().getHeader(WebhookUtils.HEADER_OB_DELIVERY_ID);
            String event = message.getHeaders().getHeader(WebhookUtils.HEADER_OB_EVENT);
            String messagePayload = jacksonMapper.writeValueAsString(message.getFeverMessage());

            FeverKafkaMessage kafkaMessage = new FeverKafkaMessage();
            kafkaMessage.setPayload(new FeverKafkaPayload(FeverConfig.WEBHOOK_NAME, deliveryId, messagePayload));
            kafkaMessage.setSchema(KAFKA_SCHEMA);
            List<Header> headers = List.of(new RecordHeader("id", deliveryId.getBytes(StandardCharsets.UTF_8)));

            LOGGER.info("[FEVER WEBHOOK] Sending Kafka notification {} Event: {} Topic: {}",
                    deliveryId, event, FeverConfig.WEBHOOK_TOPIC);

            feverWebhookProducer.sendMessage(deliveryId, kafkaMessage, headers);

        } catch (JsonProcessingException e) {
            LOGGER.error("[FEVER WEBHOOK] Error converting message to JSON", e);
        } catch (KafkaClientException e) {
            LOGGER.error("[FEVER WEBHOOK] Error sending Kafka webhook message - msg: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("[FEVER WEBHOOK] Unexpected error sending Kafka webhook message", e);
        }
    }

    private static String generateSignature(String apiKey, NotificationMessageDTO message) throws JsonProcessingException {
        return GeneratorUtils.getHashSHA256(
                JsonMapper.jacksonMapper().writeValueAsString(message) + apiKey);
    }

    private static String generateSignature(String apiKey, FeverMessageDTO message) throws JsonProcessingException {
        return GeneratorUtils.getHashSHA256(
                JsonMapper.jacksonMapper().writeValueAsString(message) + apiKey);
    }

    private String getEventAndValidateMessage(WebhookFeverDTO webhookFever) {
        String obEvent = webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_EVENT);
        if (Objects.isNull(obEvent)) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        }
        String confId = Optional.ofNullable(webhookFever.getHeaders().getHeader(WebhookUtils.HEADER_OB_HOOK_ID)).orElseThrow(
                () -> ExceptionBuilder.build(ApiExternalErrorCode.BAD_REQUEST_PARAMETER));

        if (msNotificationRepository.getNotificationConfig(confId) == null) {
            throw new OneboxRestException(ApiExternalErrorCode.WEBHOOK_CONFIG_NOT_FOUND);
        }

        return obEvent;
    }

    private String getObEvent(String header) {
        return switch (header) {
            case "ENTITY_FVZONE" -> "ENTITY";
            case "USER_FVZONE" -> "ENTITY_USER";
            default -> header;
        };
    }

    private static FeverKafkaSchema buildKafkaSchema() {
        FeverKafkaSchema schema = new FeverKafkaSchema();
        schema.setName(FeverConfig.WEBHOOK_NAME + ".Value");
        schema.setOptional(false);
        schema.setType("struct");
        schema.setFields(List.of(
                new FeverKafkaSchemaField("payload", "string"),
                new FeverKafkaSchemaField("event_id", "io.debezium.data.Uuid", "string", 1),
                new FeverKafkaSchemaField("event_fqn", "string"),
                new FeverKafkaSchemaField("created_at", "io.debezium.time.ZonedTimestamp", "string", 1)));
        return schema;
    }

}
