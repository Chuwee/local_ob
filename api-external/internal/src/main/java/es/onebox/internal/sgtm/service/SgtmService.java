package es.onebox.internal.sgtm.service;

import es.onebox.common.access.AccessService;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.notification.dto.NotificationConfigDTO;
import es.onebox.common.datasources.ms.notification.repository.MsNotificationRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.GeneratorUtils;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.internal.sgtm.datasource.SgtmDatasource;
import es.onebox.internal.sgtm.dto.ChannelExternalToolDTO;
import es.onebox.internal.sgtm.dto.SgtmMessageDTO;
import es.onebox.internal.sgtm.dto.SgtmWebhookRequestDTO;
import es.onebox.internal.sgtm.enums.ChannelExternalToolsNamesDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class SgtmService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SgtmService.class);

    private final SgtmDatasource sgtmDatasource;
    private final AccessService accessService;
    private final MsNotificationRepository msNotificationRepository;
    private final EntitiesRepository entitiesRepository;

    @Autowired
    public SgtmService(SgtmDatasource sgtmDatasource, AccessService accessService, MsNotificationRepository msNotificationRepository, EntitiesRepository entitiesRepository) {
        this.sgtmDatasource = sgtmDatasource;
        this.accessService = accessService;
        this.msNotificationRepository = msNotificationRepository;
        this.entitiesRepository = entitiesRepository;
    }

    public void processWebhook(SgtmWebhookRequestDTO request, HttpServletRequest httpServletRequest, List<Long> channelIds) {
        String bodyContent = getBodyContent(httpServletRequest);
        String code = request.getCode();

        WebhookHeaders headers = extractHeaders(httpServletRequest);

        LOGGER.info("[SGTM WEBHOOK] [{}] Processing webhook - Event: {}, Action: {}, DeliveryId: {}, HookId: {}, X-GTM-Server-Preview: {}",
                code, headers.event, headers.action, headers.deliveryId, headers.hookId, headers.xGtmServerPreview);

        validateOrderCode(code);

        boolean isMetaOrGoogleWebhook;
        if (headers.deliveryId == null || headers.hookId == null) {
            isMetaOrGoogleWebhook = true;
        } else {
            isMetaOrGoogleWebhook = false;
        }

        String accessToken;
        if (!isMetaOrGoogleWebhook) {
            NotificationConfigDTO config = getAndValidateNotificationConfig(headers.hookId, code);
            validatePayload(headers.signature, bodyContent, config.getApiKey(), code);
            accessToken = getAccessToken(config);
        } else {
            EntityDTO entityDTO = entitiesRepository.getByIdCached(request.getEntityId());
            accessToken = getOperatorAccessToken(entityDTO.getOperator().getId());
        }

        try {

            HashMap orderDetail = getOrderDetail(code, accessToken);
            Long orderChannelId = getOrderChannelId(orderDetail, code);

            if (!validateChannel(channelIds, orderChannelId, code)) {
                throw new SkipProcessingException();
            }
            List<ChannelExternalToolDTO> externalTools = request.getActiveExternalTools();
            SgtmMessageDTO sgtmMessage = createSgtmMessage(code, headers, orderDetail, externalTools);
            sgtmDatasource.sendToSgtm(sgtmMessage, headers.xGtmServerPreview);
            LOGGER.info("[SGTM WEBHOOK] [{}] Successfully sent order details to SGTM endpoint", code);
        } catch (Exception e) {
            LOGGER.error("[SGTM WEBHOOK] [{}] Error processing webhook: {}", code, e.getMessage(), e);
            throw e;
        }
    }

    // Backward compatibility for old callers
    public void processWebhook(SgtmWebhookRequestDTO request, HttpServletRequest httpServletRequest) {
        processWebhook(request, httpServletRequest, null);
    }

    private static class WebhookHeaders {
        String action;
        String event;
        String deliveryId;
        String hookId;
        String signature;
        String xGtmServerPreview;
    }

    private WebhookHeaders extractHeaders(HttpServletRequest request) {
        WebhookHeaders headers = new WebhookHeaders();
        headers.action = extractHeaderValue(request, "ob-action");
        headers.event = extractHeaderValue(request, "ob-event");
        headers.deliveryId = extractHeaderValue(request, "ob-delivery-id");
        headers.hookId = extractHeaderValue(request, "ob-hook-id");
        headers.signature = extractHeaderValue(request, "ob-signature");
        headers.xGtmServerPreview = extractHeaderValue(request, "x-gtm-server-preview");
        return headers;
    }

    private void validateOrderCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            LOGGER.error("[SGTM WEBHOOK] Order code is missing or empty");
            throw ExceptionBuilder.build(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        }
    }

    private NotificationConfigDTO getAndValidateNotificationConfig(String hookId, String code) {
        NotificationConfigDTO config = msNotificationRepository.getNotificationConfig(hookId);
        if (config == null) {
            LOGGER.error("[SGTM WEBHOOK] [{}] webhook config not found", code);
            throw ExceptionBuilder.build(ApiExternalErrorCode.WEBHOOK_CONFIG_NOT_FOUND);
        }
        return config;
    }

    private String getAccessToken(NotificationConfigDTO config) {
        return accessService.getAccessToken(config.getEntityId());
    }

    private String getOperatorAccessToken(Long entityId) {
        return accessService.getOperatorAccessToken(entityId);
    }

    private HashMap getOrderDetail(String code, String accessToken) {
        HashMap orderDetail = sgtmDatasource.getOrderDetail(code, accessToken);
        return orderDetail;
    }

    private SgtmMessageDTO createSgtmMessage(String code, WebhookHeaders headers, HashMap orderDetail,
                                             List<ChannelExternalToolDTO> externalTools) {
        SgtmMessageDTO sgtmMessage = new SgtmMessageDTO();
        sgtmMessage.setCode(code);
        sgtmMessage.setAction(headers.action);
        sgtmMessage.setEvent(headers.event);
        sgtmMessage.setOrderDetail(orderDetail);
        setActiveExternalToolsToMessage(sgtmMessage, externalTools);
        return sgtmMessage;
    }

    private String extractHeaderValue(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        if (value == null) {
            LOGGER.warn("[SGTM WEBHOOK] Missing header: {}", headerName);
        }
        return value;
    }

    private void validatePayload(String headerSignature, String bodyContent, String apiKey, String orderCode) {
        String signature = GeneratorUtils.getHashSHA256(bodyContent + apiKey);
        if (headerSignature == null || !headerSignature.equals(signature)) {
            LOGGER.error("[SGTM WEBHOOK] [{}] invalid payload for order, signature does not match. Header signature: {}, Expected signature: {}", orderCode, headerSignature, signature);
            throw ExceptionBuilder.build(ApiExternalErrorCode.INVALID_WEBHOOK_SIGNATURE);
        }
    }

    private String getBodyContent(HttpServletRequest request) {
        try {
            return IOUtils.toString(request.getReader());
        } catch (IOException e) {
            LOGGER.error("[SGTM WEBHOOK] error while reading request body", e);
            throw ExceptionBuilder.build(ApiExternalErrorCode.GENERIC_ERROR);
        }
    }

    /**
     * Returns true if the order should be processed (no filter or channel matches), false if it should be ignored.
     */
    private boolean validateChannel(List<Long> channelIds, Long orderChannelId, String code) {
        if (channelIds == null || channelIds.isEmpty()) {
            return true;
        }
        boolean match = orderChannelId != null && channelIds.contains(orderChannelId);
        if (!match) {
            LOGGER.info("[SGTM WEBHOOK] [{}] Order channel id {} not in allowed list {}, skipping processing and returning 202.", code, orderChannelId, channelIds);
        }
        return match;
    }

    /**
     * Extracts the channel id from the order detail HashMap, or returns null if not found or invalid.
     */
    private Long getOrderChannelId(HashMap orderDetail, String code) {
        Object channelObj = orderDetail.get("channel");
        if (!(channelObj instanceof HashMap)) {
            LOGGER.info("[SGTM WEBHOOK] [{}] No channel info in order detail, skipping processing and returning 202.", code);
            return null;
        }
        Object idObj = ((HashMap<?, ?>) channelObj).get("id");
        if (idObj instanceof Number) {
            return ((Number) idObj).longValue();
        } else if (idObj != null) {
            try {
                return Long.valueOf(idObj.toString());
            } catch (NumberFormatException e) {
                LOGGER.error("[SGTM WEBHOOK] [{}] Unable to parse channel id: {}", code, idObj);
            }
        }
        return null;
    }

    private void setActiveExternalToolsToMessage(SgtmMessageDTO sgtmMessage, List<ChannelExternalToolDTO> externalTools) {
        if (externalTools != null) {
            externalTools.forEach(e -> {
                if (Objects.requireNonNull(e.getName()) == ChannelExternalToolsNamesDTO.SGTM_META) {
                    sgtmMessage.setSgtmFacebookCredentials(e.getSgtmFacebookCredentials());
                } else if (e.getName() == ChannelExternalToolsNamesDTO.SGTM_GOOGLE_ANALYTICS) {
                    sgtmMessage.setSgtmGoogleCredentials(e.getSgtmGoogleCredentials());
                }
            });
        }
    }
} 