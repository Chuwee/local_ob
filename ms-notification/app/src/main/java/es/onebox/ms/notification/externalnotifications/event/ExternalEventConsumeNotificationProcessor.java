package es.onebox.ms.notification.externalnotifications.event;

import es.onebox.message.broker.eip.processor.DefaultProcessor;
import es.onebox.ms.notification.externalnotifications.factory.ExternalNotificationService;
import es.onebox.ms.notification.externalnotifications.factory.ExternalNotificationServiceFactory;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ExternalEventConsumeNotificationProcessor extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalEventConsumeNotificationProcessor.class);

    @Autowired
    private ExternalNotificationServiceFactory externalNotificationServiceFactory;


    @Override
    public void execute(Exchange exchange) {
        ExternalEventConsumeNotificationMessage body = exchange.getIn().getBody(ExternalEventConsumeNotificationMessage.class);

        try {

            // Controlamos si hemos llegado al ultimo reintento (peticion de atrapalo)
            Integer redeliveryMaxCounter = exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER, Integer.class);
            Integer redeliveryCounter = exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER, Integer.class);

            if (redeliveryMaxCounter != null && redeliveryCounter != null && redeliveryMaxCounter.equals(redeliveryCounter)) {
                LOGGER.info("Hemos llegado al último reintento para el evento: " + body.getEventId());
            }
            ExternalNotificationService externalNotificationService =
                    externalNotificationServiceFactory.getIntegrationService(body.getChannelId());
            externalNotificationService.notificationEvent(body);
        } catch (Exception e) {
            LOGGER.error("Error con las externas notificaciones de sesiones", e);
        }
    }
}
