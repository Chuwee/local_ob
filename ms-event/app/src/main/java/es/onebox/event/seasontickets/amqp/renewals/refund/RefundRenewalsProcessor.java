package es.onebox.event.seasontickets.amqp.renewals.refund;

import es.onebox.event.seasontickets.service.renewals.SeasonTicketRenewalsService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefundRenewalsProcessor extends DefaultProcessor {

    private final SeasonTicketRenewalsService service;

    @Autowired
    public RefundRenewalsProcessor(SeasonTicketRenewalsService service) {
        this.service = service;
    }

    @Override
    public void execute(Exchange exchange) throws Exception {
        RefundRenewalsMessage message = exchange.getIn().getBody(RefundRenewalsMessage.class);
        service.refundRenewal(message.getUserId(), message.getSeasonTicketId(), message.getRenewalId(), message.getOrderCode());
    }
}
