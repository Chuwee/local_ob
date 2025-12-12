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
            processProviderPlanSettingsUpdate(httpServletRequest, channelIds);
            return;
        }
        
        // Handle other webhook types (existing logic would go here)
        LOGGER.info("Webhook action {} not specifically handled, processing as standard webhook", headers.action);
    }
    
    private void processProviderPlanSettingsUpdate(HttpServletRequest httpServletRequest, List<Long> channelIds) {
        // Extract event ID and provider plan settings from headers
        String eventIdHeader = extractHeaderValue(httpServletRequest, "ob-event-id");
        String providerPlanSettings = extractHeaderValue(httpServletRequest, "ob-provider-plan-settings");
        
        if (eventIdHeader == null) {
            LOGGER.error("Event ID header (ob-event-id) is required for provider plan settings update");
            throw new IllegalArgumentException("Event ID is required");
        }
        
        Long eventId;
        try {
            eventId = Long.parseLong(eventIdHeader);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid event ID format: {}", eventIdHeader);
            throw new IllegalArgumentException("Invalid event ID format", e);
        }
        
        LOGGER.info("Processing provider plan settings update for event: {}, channelIds: {}", 
                   eventId, channelIds);
        
        if (channelIds == null || channelIds.isEmpty()) {
            LOGGER.error("At least one channel ID is required for provider plan settings update");
            throw new IllegalArgumentException("At least one channel ID is required");
        }
        
        // Forward to fever for each channel
        for (Long channelId : channelIds) {
            providerPlanSettingsService.sendProviderPlanSettingsToFever(
                eventId,
                channelId,
                providerPlanSettings
            );
        }
        
        LOGGER.info("Successfully processed provider plan settings update for event: {}", eventId);
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
