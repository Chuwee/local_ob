package es.onebox.internal.automaticrenewals.eip.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.internal.automaticrenewals.renewals.enums.AutomaticRenewalsStatus;
import es.onebox.internal.automaticrenewals.renewals.service.AutomaticRenewalsExecutorService;
import es.onebox.internal.automaticrenewals.renewals.service.AutomaticRenewalsHazelcastService;
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
public class AutomaticRenewalsConsumer extends DefaultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AutomaticRenewalsConsumer.class);

    private final ObjectMapper jacksonMapper;
    private final AutomaticRenewalsExecutorService automaticRenewalsService;
    private final AutomaticRenewalsHazelcastService automaticRenewalsHazelcastService;

    @Autowired
    public AutomaticRenewalsConsumer(ObjectMapper jacksonMapper,
                                     AutomaticRenewalsExecutorService automaticRenewalsService,
                                     AutomaticRenewalsHazelcastService automaticRenewalsHazelcastService) {
        this.jacksonMapper = jacksonMapper;
        this.automaticRenewalsService = automaticRenewalsService;
        this.automaticRenewalsHazelcastService = automaticRenewalsHazelcastService;
    }

    @Override
    public void execute(Exchange exchange)  {
        Message messageRaw = exchange.getIn();
        Object body = messageRaw.getBody();
        if (body instanceof byte[]) {
            AutomaticRenewalsMessage message;
            try {
                message = jacksonMapper.readValue((byte[]) body, AutomaticRenewalsMessage.class);
            } catch (Exception e) {
                LOGGER.error("[AUTOMATIC RENEWALS] - Failed to read message from queue message", e);
                throw new OneboxRestException(ApiExternalErrorCode.AUTOMATIC_RENEWAL_RETRY_ERROR);
            }
            AutomaticRenewalsStatus status = automaticRenewalsHazelcastService.getStatus(message.getSeasonTicketId());
            if (AutomaticRenewalsStatus.IN_PROGRESS.equals(status)) {
                LOGGER.error("[AUTOMATIC RENEWALS] - Process for season ticket {} already in progress", message.getSeasonTicketId());
                throw new OneboxRestException(ApiExternalErrorCode.ALREADY_EXECUTING_AUTOMATIC_RENEWALS);
            }
            try {
                automaticRenewalsService.execute(message);
            } catch (Exception e) {
                LOGGER.error("[AUTOMATIC RENEWALS] - Failed automatic renewal execution", e);
                automaticRenewalsHazelcastService.setStatus(message.getSeasonTicketId(), AutomaticRenewalsStatus.ERROR);
                throw new OneboxRestException(ApiExternalErrorCode.AUTOMATIC_RENEWAL_RETRY_ERROR);
            }
        } else {
            LOGGER.debug("[AUTOMATIC RENEWALS] - Invalid AutomaticSalesProcess queue message {}", body);
        }
    }
}
