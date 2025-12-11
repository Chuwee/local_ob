package es.onebox.event.seasontickets.amqp.renewals.revert;

import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RevertRenewalProcessor extends DefaultProcessor {

    private final SeasonTicketRenewalsService service;

    @Autowired
    public RevertRenewalProcessor(SeasonTicketRenewalsService service) {
        this.service = service;
    }

    @Override
    public void execute(Exchange exchange) throws Exception {
        RevertRenewalMessage message = exchange.getIn().getBody(RevertRenewalMessage.class);

        if(message.getItems() != null && !message.getItems().isEmpty()) {
            message.getItems().forEach(item -> {
                Long seasonTicketId = item.getSeasonTicketId();
                String userId = item.getUserId();
                String id = item.getId();
                service.revertBlockedRenewals(seasonTicketId, userId, id);
            });
        }
    }
}
