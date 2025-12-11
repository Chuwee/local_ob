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
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    @Qualifier("providerPlanSettingsNotificationProducer")
    private DefaultProducer providerPlanSettingsNotificationProducer;

    public void sendProviderPlanSettingsNotification(Long eventId, Long channelId, ProviderPlanSettings providerPlanSettings) {
        if (providerPlanSettings == null) {
            LOGGER.debug("Skipping provider plan settings notification - no settings provided");
            return;
        }

        ProviderPlanSettingsNotificationMessage message = new ProviderPlanSettingsNotificationMessage();
        message.setEventId(eventId);
        message.setChannelId(channelId);
        
        try {
            String settingsJson = OBJECT_MAPPER.writeValueAsString(providerPlanSettings);
            message.setProviderPlanSettings(settingsJson);
            
            providerPlanSettingsNotificationProducer.sendMessage(message);
            LOGGER.info("Provider plan settings notification sent for event {} and channel {}", eventId, channelId);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] ProviderPlanSettingsNotification message could not be sent for event {} and channel {}", 
                       eventId, channelId, e);
        }
    }
}
