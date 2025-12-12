package es.onebox.internal.automaticsales.eip.email;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailSenderQueueProducer extends DefaultProducer {

    private static final Logger LOG = LoggerFactory.getLogger(EmailSenderQueueProducer.class);

    @Autowired
    public EmailSenderQueueProducer(@Value("${amqp.send-email.name}") String queueName) {
        super(queueName, true);
    }

    public void sendMessage(final SendEmailMessage message) {
        try {
            super.sendMessage(message);
        } catch (Exception e) {
            LOG.error("Error sending message to AMQP", e);
            throw OneboxRestException.builder(ApiExternalErrorCode.AMQP_PUSH_EXCEPTION).build();
        }
    }

}
