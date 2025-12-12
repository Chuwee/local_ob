package es.onebox.internal.xmlsepa.eip.sepa;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.internal.xmlsepa.service.sepa.SEPADirectDebitService;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SEPADirectDebitConsumer extends DefaultProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SEPADirectDebitConsumer.class);

    private final ObjectMapper objectMapper;
    private final SEPADirectDebitService sepaDirectDebitService;

    @Autowired
    public SEPADirectDebitConsumer(ObjectMapper objectMapper, SEPADirectDebitService sepaDirectDebitService) {
        this.objectMapper = objectMapper;
        this.sepaDirectDebitService = sepaDirectDebitService;
    }

    @Override
    public void execute(Exchange exchange)  {
        Message messageRaw = exchange.getIn();
        Object body = messageRaw.getBody();
        if (body instanceof byte[]) {
            try {
                SEPADirectDebitMessage message = objectMapper.readValue((byte[]) body, SEPADirectDebitMessage.class);
                sepaDirectDebitService.processSEPADirectDebit(message.getSeasonTicketId(), message.getUserId());
            } catch (Exception e) {
                LOG.error("[PROCESS SEPA] - Failed to read create AutomaticRenewalSEPA message from queue message",e);
                throw new OneboxRestException(ApiExternalErrorCode.AUTOMATIC_RENEWAL_RETRY_ERROR);
            }
        } else {
            LOG.debug("[PROCESS SEPA] - Invalid AutomaticRenewalSEPA queue message {}", body);
        }
    }
}
