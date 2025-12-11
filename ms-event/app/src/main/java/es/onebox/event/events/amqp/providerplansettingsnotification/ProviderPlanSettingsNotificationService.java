package es.onebox.event.events.amqp.providerplansettingsnotification;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.event.events.dto.ProviderPlanSettings;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProviderPlanSettingsNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderPlanSettingsNotificationService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("providerPlanSettingsNotificationProducer")
    private DefaultProducer providerPlanSettingsNotificationProducer;

    public void sendProviderPlanSettingsNotification(Long eventId, Long channelId, ProviderPlanSettings providerPlanSettings) {
        ProviderPlanSettingsNotificationMessage message = new ProviderPlanSettingsNotificationMessage();
        message.setEventId(eventId);
        message.setChannelId(channelId);
        
        try {
            // Serialize settings to JSON, or set to null if settings are null (to signal clearing)
            String settingsJson = providerPlanSettings != null 
                ? objectMapper.writeValueAsString(providerPlanSettings) 
                : null;
            message.setProviderPlanSettings(settingsJson);
            
            providerPlanSettingsNotificationProducer.sendMessage(message);
            LOGGER.info("Provider plan settings notification sent for event {} and channel {} (settings={})", 
                       eventId, channelId, settingsJson != null ? "updated" : "cleared");
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] ProviderPlanSettingsNotification message could not be sent for event {} and channel {}", 
                       eventId, channelId, e);
        }
    }
}
