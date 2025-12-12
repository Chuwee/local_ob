package es.onebox.internal.automaticsales.mailing;

import es.onebox.internal.automaticsales.eip.email.EmailSenderQueueProducer;
import es.onebox.internal.automaticsales.eip.email.SendEmailMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailingService {

    private final EmailSenderQueueProducer emailSender;

    @Autowired
    public MailingService(EmailSenderQueueProducer emailSender) {
        this.emailSender = emailSender;
    }

    public void sendReport(final String email, final String body, final String subject) {
        if (StringUtils.isNotBlank(email)) {
            this.emailSender.sendMessage(SendEmailMessage.builder().targetEmail(email).subject(subject).body(body).build());
        }
    }
}
