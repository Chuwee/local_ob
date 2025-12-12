package es.onebox.internal.sgtm.service;

import es.onebox.internal.sgtm.dto.SgtmWebhookRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SgtmService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SgtmService.class);
    private static final String ACTION_PROVIDER_PLAN_SETTINGS_UPDATE = "PROVIDER_PLAN_SETTINGS_UPDATE";
    private static final String EVENT_EVENT_CHANNEL = "EVENT_CHANNEL";
    
    private final ProviderPlanSettingsService providerPlanSettingsService;

    @Autowired
    public SgtmService(ProviderPlanSettingsService providerPlanSettingsService) {
        this.providerPlanSettingsService = providerPlanSettingsService;
    }

    public void processWebhook(SgtmWebhookRequestDTO webhookRequest, HttpServletRequest httpServletRequest, List<Long> channelIds) {
        WebhookHeaders headers = extractHeaders(httpServletRequest);
        
        LOGGER.info("Processing webhook with action: {} and event: {}", headers.action, headers.event);
        
        // Handle provider plan settings updates
        if (ACTION_PROVIDER_PLAN_SETTINGS_UPDATE.equals(headers.action) && EVENT_EVENT_CHANNEL.equals(headers.event)) {
            processProviderPlanSettingsUpdate(webhookRequest, channelIds);
            return;
        }
        
        // Handle other webhook types (existing logic would go here)
        LOGGER.info("Webhook action {} not specifically handled, processing as standard webhook", headers.action);
    }
    
    private void processProviderPlanSettingsUpdate(SgtmWebhookRequestDTO webhookRequest, List<Long> channelIds) {
        LOGGER.info("Processing provider plan settings update for event: {}, channelIds: {}", 
                   webhookRequest.getEventId(), channelIds);
        
        if (webhookRequest.getEventId() == null) {
            LOGGER.error("Event ID is required for provider plan settings update");
            throw new IllegalArgumentException("Event ID is required");
        }
        
        if (channelIds == null || channelIds.isEmpty()) {
            LOGGER.error("At least one channel ID is required for provider plan settings update");
            throw new IllegalArgumentException("At least one channel ID is required");
        }
        
        // Forward to fever for each channel
        for (Long channelId : channelIds) {
            providerPlanSettingsService.sendProviderPlanSettingsToFever(
                webhookRequest.getEventId(),
                channelId,
                webhookRequest.getProviderPlanSettings()
            );
        }
        
        LOGGER.info("Successfully processed provider plan settings update for event: {}", webhookRequest.getEventId());
    }
    
    WebhookHeaders extractHeaders(HttpServletRequest httpServletRequest) {
        WebhookHeaders headers = new WebhookHeaders();
        headers.action = extractHeaderValue(httpServletRequest, "ob-action");
        headers.event = extractHeaderValue(httpServletRequest, "ob-event");
        headers.deliveryId = extractHeaderValue(httpServletRequest, "ob-delivery-id");
        headers.hookId = extractHeaderValue(httpServletRequest, "ob-hook-id");
        headers.signature = extractHeaderValue(httpServletRequest, "ob-signature");
        headers.xGtmServerPreview = extractHeaderValue(httpServletRequest, "x-gtm-server-preview");
        return headers;
    }
    
    String extractHeaderValue(HttpServletRequest httpServletRequest, String headerName) {
        return httpServletRequest.getHeader(headerName);
    }
    
    private static class WebhookHeaders {
        String action;
        String event;
        String deliveryId;
        String hookId;
        String signature;
        String xGtmServerPreview;
    }
}
