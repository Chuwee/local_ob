package es.onebox.ms.notification.webhooks.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.datasource.http.RequestHeaders;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.ms.notification.datasources.ms.channel.dto.Channel;
import es.onebox.ms.notification.datasources.ms.channel.dto.ChannelExternalToolsDTO;
import es.onebox.ms.notification.datasources.ms.channel.enums.ChannelExternalToolsNamesDTO;
import es.onebox.ms.notification.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.ms.notification.exception.MsNotificationErrorCode;
import es.onebox.ms.notification.webhooks.WebhookSendingService;
import es.onebox.ms.notification.webhooks.WebhookService;
import es.onebox.ms.notification.webhooks.dto.EventNotificationMessage;
import es.onebox.ms.notification.webhooks.dto.ExternalApiWebhookDto;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigDTO;
import es.onebox.ms.notification.webhooks.dto.NotificationMessageDTO;
import es.onebox.ms.notification.webhooks.dto.WrapperDTO;
import es.onebox.ms.notification.webhooks.enums.NotificationAction;
import es.onebox.ms.notification.webhooks.enums.NotificationType;
import es.onebox.ms.notification.webhooks.enums.NotificationsStatus;
import es.onebox.ms.notification.webhooks.enums.OrderAction;
import es.onebox.ms.notification.webhooks.utils.NotifierDispatcher;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.apache.camel.Exchange;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static es.onebox.ms.notification.webhooks.queue.WebhookNotificationsConfiguration.HEADER_REDELIVERY_COUNTER;
import static es.onebox.ms.notification.webhooks.queue.WebhookNotificationsConfiguration.HEADER_REDELIVERY_LIMIT;
import static es.onebox.ms.notification.webhooks.queue.error.WebhookNotificationErrorProcessor.WEBHOOK_ERROR_RETRIES;

@Component
public class WebhookNotificationsProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookNotificationsProcessor.class);

    protected static final RetryPolicy RETRY_POLICY = new RetryPolicy().retryOn(Exception.class)
            .withBackoff(1, 10, TimeUnit.SECONDS)
            .withMaxRetries(3);

    private final NotifierDispatcher dispatcher;
    private final WebhookService webhookService;
    private final WebhookSendingService webhookSendingService;
    private final DefaultProducer webhookNotificationErrorProducer;
    private final ChannelRepository channelRepository;

    @Autowired
    public WebhookNotificationsProcessor(NotifierDispatcher dispatcher,
                                         WebhookService webhookService,
                                         WebhookSendingService webhookSendingService,
                                         @Qualifier("webhookNotificationErrorProducer") DefaultProducer webhookNotificationErrorProducer, ChannelRepository channelRepository) {
        this.dispatcher = dispatcher;
        this.webhookService = webhookService;
        this.webhookSendingService = webhookSendingService;
        this.webhookNotificationErrorProducer = webhookNotificationErrorProducer;
        this.channelRepository = channelRepository;
    }

    @Override
    public void execute(Exchange exchange) throws Exception {
        EventNotificationMessage message = exchange.getIn().getBody(EventNotificationMessage.class);
        String messageKey;


        if (message.getOrderCode() != null) {
            messageKey = message.getOrderCode();
        } else if (message.getPromotionId() != null) {
            messageKey = message.getPromotionId().toString();
        } else if (message.getB2bMovementId() != null) {
            messageKey = message.getB2bMovementId();
        } else {
            messageKey = message.getId().toString();
        }

        try {
            WrapperDTO wrapperDTO = dispatcher.getWrapper(message);

            List<NotificationConfigDTO> notificationConfigs = getNotificationConfig(wrapperDTO);

            if (!CollectionUtils.isEmpty(notificationConfigs)) {
                for (NotificationConfigDTO config : notificationConfigs) {
                    NotificationType type = NotificationType.valueOf(wrapperDTO.getPayloadRequest().getEvent());
                    if (!eventNotificationEnabled(wrapperDTO, config, type)) {
                        continue;
                    }
                    switch (config.getScope()) {
                        case ENTITY, SYS_ADMIN -> sendWebHookMessage(messageKey, type, config, wrapperDTO);
                        case CHANNEL -> {
                            if (Objects.equals(config.getChannelId(), wrapperDTO.getChannelId())) {
                                sendWebHookMessage(messageKey, type, config, wrapperDTO);
                            }
                        }
                    }
                }
            }


            Long channelId;
            if (message.getChannelId() == null) {
                channelId = wrapperDTO.getChannelId();
            } else {
                channelId = message.getChannelId();
            }
            ChannelExternalToolsDTO externalToolsDTO = webhookService.getChannelExternalTool(channelId);

            boolean shouldProcess = false;
            if (externalToolsDTO != null) {
                shouldProcess = externalToolsDTO.stream()
                        .anyMatch(e ->
                                (ChannelExternalToolsNamesDTO.SGTM_META.equals(e.getName()) ||
                                        ChannelExternalToolsNamesDTO.SGTM_GOOGLE_ANALYTICS.equals(e.getName()))
                                        && Boolean.TRUE.equals(e.getEnabled())
                        );
            }
            if (shouldProcess) {
                message.setChannelId(channelId);
                sendToServerTagManager(message, externalToolsDTO);
            }
        } catch (Exception e) {
            LOGGER.error("[WEBHOOK] [{}] event: {} - Error processing notification", messageKey, message.getEvent(), e);
            handleRetries(exchange, message);
            throw e;
        }
    }

    private void handleRetries(Exchange exchange, EventNotificationMessage message) throws Exception {
        Map<String, Object> headers = exchange.getIn().getHeaders();
        Object redeliveryCounter = headers.get(HEADER_REDELIVERY_COUNTER);
        if (message.getEvent().equals("ORDER") &&
                redeliveryCounter != null && redeliveryCounter.equals(headers.get(HEADER_REDELIVERY_LIMIT))) {
            Integer errorRetries = (Integer) headers.getOrDefault(WEBHOOK_ERROR_RETRIES, 0);
            LOGGER.warn("[WEBHOOK] code: {} - Retry limits exceeded, enqueue into webhook-error retry queue #{}",
                    message.getOrderCode(), errorRetries);
            headers.put(WEBHOOK_ERROR_RETRIES, ++errorRetries);
            webhookNotificationErrorProducer.sendMessage(message, headers);
        }
    }

    private static boolean eventNotificationEnabled(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationType type) {
        return NotificationsStatus.ACTIVE.equals(config.getStatus()) &&
                config.getEvents().containsKey(type) &&
                checkActionsByType(wrapperDTO, config, type);
    }

    private static boolean checkActionsByType(WrapperDTO wrapperDTO, NotificationConfigDTO config, NotificationType type) {
        if (NotificationAction.RELOCATE.equals(wrapperDTO.getPayloadRequest().getAction())) {
            return config.getEvents().get(type).contains(NotificationAction.PURCHASE.name());
        }
        return config.getEvents().get(type).contains(wrapperDTO.getPayloadRequest().getAction().name());
    }

    private void sendWebHookMessage(String key, NotificationType type, NotificationConfigDTO config, WrapperDTO wrapperDTO) throws JsonProcessingException {
        String webhookEvent = wrapperDTO.getPayloadRequest().getEvent();
        LOGGER.info("[WEBHOOK] [{}] event: {} - Processing notification of entity: {}", key, webhookEvent, wrapperDTO.getEntityId());

        NotificationMessageDTO messageDTO = dispatcher.generateMessage(wrapperDTO, config, type);

        Failsafe.with(RETRY_POLICY)
                .onRetry((c, failure, ctx)
                        -> LOGGER.warn(" [WEBHOOK] [{}] Error sending webhook. Error: {}, Retrying {}", key, failure, ctx.getExecutions())
                )
                .onRetriesExceeded(ctx
                        -> LOGGER.error(" [WEBHOOK] [{}] Error sending webhook. Max retries exceeded.", key)
                )
                .withFallback(() -> null)
                .get(() -> webhookSendingService.sendNotification(config.getUrl(), messageDTO));
    }

    private List<NotificationConfigDTO> getNotificationConfig(WrapperDTO wrapperDTO) {
        Long entityId = wrapperDTO.getEntityId();
        String defaultDocumentId = DefaultNotificationConfigs.getDocumentId(
                wrapperDTO.getPayloadRequest().getEvent(), wrapperDTO.getPayloadRequest().getAction());
        if (defaultDocumentId != null) {
            NotificationConfigDTO notificationConfig = webhookService.getNotificationConfig(defaultDocumentId);
            if (notificationConfig != null) {
                notificationConfig.setEntityId(wrapperDTO.getEntityId());
                return List.of(notificationConfig);
            }
        }
        return webhookService.getNotificationConfigs(entityId);
    }

    private void sendToServerTagManager(EventNotificationMessage message, ChannelExternalToolsDTO externalToolsDTO) {
        Long channelId = message.getChannelId();
        Channel channel = channelRepository.getChannel(channelId);

        if (channel == null) {
            throw new OneboxRestException(MsNotificationErrorCode.CHANNEL_NOT_FOUND);
        }

        Long entityId = channel.getEntityId();

        ChannelExternalToolsDTO onlyActiveExternalTools = externalToolsDTO.stream()
                .filter(e ->
                        e.getName().equals(ChannelExternalToolsNamesDTO.SGTM_META) ||
                                e.getName().equals(ChannelExternalToolsNamesDTO.SGTM_GOOGLE_ANALYTICS))
                .filter(e -> BooleanUtils.isTrue(e.getEnabled()))
                .collect(Collectors.toCollection(ChannelExternalToolsDTO::new));

        ExternalApiWebhookDto externalApiWebhookDto = new ExternalApiWebhookDto();
        externalApiWebhookDto.setActiveExternalTools(onlyActiveExternalTools);
        externalApiWebhookDto.setCode(message.getOrderCode());
        externalApiWebhookDto.setEntityId(entityId);

        WrapperDTO wrapperDTO = dispatcher.getWrapper(message);

        RequestHeaders headers = new RequestHeaders.Builder()
                .addHeader("ob-action", OrderAction.PURCHASE.name())
                .addHeader("ob-event", wrapperDTO.getPayloadRequest().getEvent())
                .build();

        webhookSendingService.sendNotificationToApiExternal(channelId, headers, externalApiWebhookDto);

    }
}
