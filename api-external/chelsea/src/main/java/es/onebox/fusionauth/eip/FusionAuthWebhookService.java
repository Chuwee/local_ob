package es.onebox.fusionauth.eip;

import es.onebox.fusionauth.dto.FusionAuthUserDTO;
import es.onebox.fusionauth.enums.WebhookType;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FusionAuthWebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FusionAuthWebhookService.class);

    @Autowired
    private DefaultProducer fusionAuthWebhookProducer;

    public void fusionAuthWebhookProducer(Object customer, WebhookType type, Object originalCustomer) {
        fusionAuthWebhookProducer(createMessage(customer, type, originalCustomer));
    }


    private void fusionAuthWebhookProducer(FusionAuthWebhookMessage fusionAuthWebhookMessage) {
        try {
            this.fusionAuthWebhookProducer.sendMessage(fusionAuthWebhookMessage);
        } catch (Exception e) {
            LOGGER.warn("[AMQP CLIENT] FusionAuth webhook event message can't be sent ", e);
        }
    }

    private FusionAuthWebhookMessage createMessage(Object customer, WebhookType type, Object originalCustomer) {
        FusionAuthWebhookMessage message = new FusionAuthWebhookMessage();
        message.setType(type);
        message.setUser(customer);
        if(originalCustomer != null) {
            message.setOriginalUser(originalCustomer);
        }
        return message;
    }
}