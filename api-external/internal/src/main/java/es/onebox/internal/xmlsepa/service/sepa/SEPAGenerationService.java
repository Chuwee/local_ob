package es.onebox.internal.xmlsepa.service.sepa;

import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.internal.xmlsepa.converter.SEPAConverter;
import es.onebox.message.broker.producer.queue.DefaultProducer;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import es.onebox.oauth2.resource.utils.TokenParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SEPAGenerationService {
    private static final Logger LOG = LoggerFactory.getLogger(SEPAGenerationService.class);

    private final DefaultProducer sepaGenerationProducer;

    @Autowired
    public SEPAGenerationService(DefaultProducer sepaGenerationProducer) {
        this.sepaGenerationProducer = sepaGenerationProducer;
    }

    public void sendRenewalMessage(Long seasonTicketId) {
        try {
            sepaGenerationProducer.sendMessage(SEPAConverter.toMessage(seasonTicketId, AuthContextUtils.getLongAttr(TokenParam.USER_ID.value())));
        } catch (Exception e) {
            LOG.error("Error sending SEPA generation message to AMQP for seasonTicketId: {}", seasonTicketId, e);
            throw OneboxRestException.builder(ApiExternalErrorCode.AMQP_PUSH_EXCEPTION).build();
        }
    }
} 