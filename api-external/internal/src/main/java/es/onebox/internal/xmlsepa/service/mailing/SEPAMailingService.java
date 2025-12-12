package es.onebox.internal.xmlsepa.service.mailing;

import es.onebox.internal.xmlsepa.eip.email.SEPAEmailSenderQueueProducer;
import es.onebox.internal.xmlsepa.eip.email.SEPASendEmailMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SEPAMailingService {

    private final SEPAEmailSenderQueueProducer emailSender;

    @Autowired
    public SEPAMailingService(SEPAEmailSenderQueueProducer emailSender) {
        this.emailSender = emailSender;
    }

    public void sendXMLSEPA(final String email, final String body, final String subject) {
        if (StringUtils.isNotBlank(email)) {
            this.emailSender.sendMessage(SEPASendEmailMessage.builder().targetEmail(email).subject(subject).body(body).build());
        }
    }
}
