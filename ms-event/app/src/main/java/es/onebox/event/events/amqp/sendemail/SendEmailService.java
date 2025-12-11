package es.onebox.event.events.amqp.sendemail;

import es.onebox.message.broker.producer.queue.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendEmailService.class);

    @Autowired
    @Qualifier("sendEmailProducer")
    private DefaultProducer sendEmailProducer;

    public void sendEmail(String targetEmail, String subject, String body, Integer channelId, EmailType emailType,
                          Map<String, byte[]> attachments) {
        SendEmailMessage sendEmailMessage = new SendEmailMessage();
        sendEmailMessage.setTargetEmail(targetEmail);
        sendEmailMessage.setSubject(subject);
        sendEmailMessage.setBody(body);
        sendEmailMessage.setChannelId(channelId);
        sendEmailMessage.setAttachments(attachments);
        sendEmailMessage.setEmailType(emailType);
        sendEmail(sendEmailMessage);
    }

    public void sendEmail(SendEmailMessage sendEmailMessage) {
        try {
            sendEmailProducer.sendMessage(sendEmailMessage);
        } catch (Exception e) {
            LOGGER.error("[AMQP CLIENT] SendEmail Message could not be send", e);
        }
    }


}
